package com.multimoney.multimoney.util.firebase

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.multimoney.multimoney.presentation.util.createNotificationChannel
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
            Log.d("FCM", "FCM registration token " + task.result)
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
}
