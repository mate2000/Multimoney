package com.multimoney.multimoney.presentation.ui

import android.content.Intent
import com.facebook.react.bridge.*

class PackageTrackerModule (reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    @ReactMethod
    fun callback(error: Boolean, response: String) {
        val intent = Intent()
        intent.putExtra(RESPONSE_VALUE, response)
        intent.putExtra(RESPONSE_IS_ERROR, error)
        currentActivity?.setResult(RESULT_CODE_PROCESS_FINISHED, intent)
        currentActivity?.finish()
    }

    @ReactMethod
    fun onClose() {
        currentActivity?.setResult(RESULT_CODE_PROCESS_INCOMPLETE)
        currentActivity?.finish()
    }

    override fun getName(): String {
        return "PackageTrackerModule"
    }

    companion object {
        const val RESULT_CODE_PROCESS_FINISHED = 200
        const val RESULT_CODE_PROCESS_INCOMPLETE = 400
        const val RESPONSE_VALUE = "response_value_key"
        const val RESPONSE_IS_ERROR = "response_error_key"
    }

}