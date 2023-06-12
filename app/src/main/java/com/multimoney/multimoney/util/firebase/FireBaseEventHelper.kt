package com.multimoney.multimoney.util.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FireBaseEventHelper(val context: Context) {

    fun logEvent(event: FireBaseEvents) {
        val params = Bundle()
        params.putString(event.eventName, event.parametersValue)
        FirebaseAnalytics.getInstance(context).logEvent(event.event, params)
    }
}