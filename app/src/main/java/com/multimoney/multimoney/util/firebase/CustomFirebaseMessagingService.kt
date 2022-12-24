package com.multimoney.multimoney.util.firebase

import android.content.Intent
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.multimoney.data.extension.safeLet
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.util.displayLocalNotification
import timber.log.Timber

class CustomFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val messageTitle = message.notification?.title
        val messageBody = message.notification?.body
        val intent = Intent()

        safeLet(messageTitle, messageBody) { title, body ->
            displayLocalNotification(
                title,
                body,
                intent,
                this,
                R.mipmap.ic_launcher,
                ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)?.toBitmap()
            )
        }

        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Timber.d("New Firebase Token: $newToken")
    }
}
