package com.multimoney.multimoney.util.firebase

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.multimoney.data.extension.safeLet
import com.multimoney.multimoney.R
import timber.log.Timber

/**
 * Created by rodrigomiranda on 5/13/20.
 */
class CustomFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {

        val messageTitle = message.notification?.title
        val messageBody = message.notification?.body

        val intent = Intent()
        safeLet(messageTitle, messageBody) { title, body ->
            displayLocalNotification(title, body, intent)
        }
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun displayLocalNotification(
        title: String,
        body: String,
        intent: Intent
    ) {
        val builder = NotificationCompat.Builder(this, FirebaseHelper.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(
                ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)?.toBitmap()
            )
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        builder.setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Timber.d("New Firebase Token: $newToken")
    }

    companion object {
        const val NOTIFICATION_ID = 42069
    }
}
