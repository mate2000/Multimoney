package com.multimoney.multimoney.presentation.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import com.facebook.react.bridge.*
import com.multimoney.multimoney.MultimoneyApplication
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.cards.addcard.AddCardVDViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class PackageTrackerModule (reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private var result: Result? = null

    @ReactMethod
    fun callback(error: Boolean, response: String) {
        result = currentActivity as? Result
        if (!error) {
            System.out.println("Show response card: " + response)
            AddCardVDViewModel.UIEvent.OnCallMutationCreateCardVDUseCase("test")
        } else {
            result?.onCallBackResult("TEST")
            System.out.println("Error: " + response)
            currentActivity?.setResult(1)
            currentActivity?.finish()
        }
    }

    @ReactMethod
    fun onClose() {
        System.out.println("replace with onclose event or required behavior")

    }

    override fun getName(): String {
        return "PackageTrackerModule"
    }

    fun subscribe(listener: Result) {
        result = listener
    }

    interface Result {
        fun onCallBackResult(response: String)
    }
}