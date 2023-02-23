package com.multimoney.multimoney.presentation.ui

import com.facebook.react.bridge.*
import com.google.gson.JsonParser

class PackageTrackerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    @ReactMethod
    fun callback(error: String, response: String) {
        if (error === "undefined") {
            System.out.println("Show response card: " + JsonParser.parseString(response))
        } else {
            System.out.println("Error: " + JsonParser.parseString(error))
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