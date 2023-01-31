package com.multimoney.multimoney.presentation.ui

import com.facebook.react.bridge.*

class PackageTrackerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    @ReactMethod
    fun callback(error: Boolean, response: String) {
        if (!error) {
            System.out.println("Show response card: " + response)
        } else {
            System.out.println("Error: " + response)
        }
    }

    @ReactMethod
    fun onClose() {
        System.out.println("replace with onclose event or required behavior")

    }

    override fun getName(): String {
        return "PackageTrackerModule"
    }
}