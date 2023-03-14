package com.multimoney.multimoney.util.firebase

import android.content.Intent
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.multimoney.data.extension.safeLet
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.MainActivity
import com.multimoney.multimoney.presentation.util.displayLocalNotification

class CustomFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val messageTitle = message.notification?.title
        val messageBody = message.notification?.body
        val messageData = message.data[ROUTE_ID]

        safeLet(messageTitle, messageBody) { title, body ->
            displayLocalNotification(
                title,
                body,
                getIntent(messageData),
                this,
                R.mipmap.ic_launcher,
                ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)?.toBitmap()
            )
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d("FCM", "New Firebase Token: $newToken")
    }

    private fun getIntent(routeId: String?) =
        if (routeId?.isNotBlank() == true) {
            val intent = Intent(this, MainActivity::class.java)
            intent.also {
                it.putExtra(ROUTE_ID, routeId)
            }
        } else {
            Intent()
        }

    companion object {
        private const val ROUTE_ID = "route_id"
    }
}
