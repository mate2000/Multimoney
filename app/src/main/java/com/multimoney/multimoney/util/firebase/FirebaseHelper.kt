package com.multimoney.multimoney.util.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class FirebaseHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    init {
        createNotificationChannel(context)
    }

    fun getInstallationId(onResult: (installationId: String?) -> Unit) =
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful.not()) {
                Timber.e("Installations", "Unable to get Installation ID")
                return@addOnCompleteListener
            }
            Timber.d("Installations", "Installation ID: " + task.result)
            onResult(task.result)
        }

    fun getInstallationToken(onResult: (installationToken: String?) -> Unit) =
        FirebaseInstallations.getInstance().getToken(false).addOnCompleteListener { task ->
            if (task.isSuccessful.not()) {
                Timber.e("Installations", "Unable to get Installation auth token")
                return@addOnCompleteListener
            }
            Timber.d("Installations", "Installation auth token: " + task.result?.token)
            onResult(task.result.token)
        }

    fun registerFCMDevice(onResult: (firebaseToken: String?) -> Unit) {
        enableFCM()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful.not()) {
                Timber.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            Timber.w(TAG, "FCM registration token", task.result)
            onResult(task.result)
        }
    }

    fun unregisterFCMDevice() {
        disableFCM()
        FirebaseInstallations.getInstance().delete().addOnCompleteListener { task ->
            if (task.isComplete.not()) {
                Timber.e("Installations", "Unable to delete Installation")
                return@addOnCompleteListener
            }
            Timber.d("Installations", "Installation deleted")
        }
    }

    /**
     * Enable Firebase Cloud Message using auto-init; this service will generate a new token
     * and it will be received in the [com.multimoney.multimoney.util.firebase]
     */
    private fun enableFCM() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    /**
     * Firebase Cloud Message auto-init is disabled by default in the AndroidManifest.xml file
     * but it is also necessary to call this before "deleteInstanceId()" b/c it will prevent
     * receive new tokens in [com.multimoney.multimoney.util.firebase]
     */
    private fun disableFCM() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
    }

    private fun createNotificationChannel(context: Context) {
        Timber.i("createNotificationChannel")
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "multimoney.channel.id"
        private const val CHANNEL_NAME = "multimoney.notifications"
        private const val CHANNEL_DESCRIPTION = "multimoney notification channel"
    }
}