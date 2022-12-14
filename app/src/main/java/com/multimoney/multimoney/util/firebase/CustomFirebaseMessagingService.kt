package com.multimoney.multimoney.util.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.multimoney.data.extension.safeLet
import com.multimoney.multimoney.R
import okhttp3.internal.notify
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
                ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)?.toBitmap(),
                R.mipmap.ic_launcher
            )
        }

        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Timber.d("New Firebase Token: $newToken")
    }

    companion object {
        const val NOTIFICATION_ID = 42069
    }
}

fun displayLocalNotification(
    title: String,
    body: String,
    intent: Intent,
    context: Context,
    largeIcon: Bitmap?,
    smallIconResource: Int
) {
    val notificationManager = NotificationManagerCompat.from(context)
    createNotificationChannel(context)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(smallIconResource)
        .setLargeIcon(largeIcon)
        .setContentTitle(title)
        .setStyle(NotificationCompat.BigTextStyle())
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    builder.setContentIntent(pendingIntent)

    notificationManager.notify(35, builder.build())
}

fun createNotificationChannel(context: Context) {
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

const val CHANNEL_ID = "multimoney.channel.id"
const val CHANNEL_NAME = "multimoney.notifications"
const val CHANNEL_DESCRIPTION = "multimoney notification channel"
