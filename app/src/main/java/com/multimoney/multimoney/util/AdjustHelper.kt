package com.multimoney.multimoney.util

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.multimoney.multimoney.BuildConfig
import com.multimoney.multimoney.BuildConfig.ADJUST_APP_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject


class AdjustHelper @Inject constructor(@ApplicationContext private val context: Context) {

    private val environment = if (BuildConfig.DEBUG) {
        AdjustConfig.ENVIRONMENT_SANDBOX
    } else {
        AdjustConfig.ENVIRONMENT_PRODUCTION
    }

    private fun getAdjustConfig(): AdjustConfig {
        val adjustConfig = AdjustConfig(context, ADJUST_APP_TOKEN, environment)
        adjustConfig.setLogLevel(LogLevel.VERBOSE)
        return adjustConfig
    }

    fun initAdjust() = Adjust.onCreate(getAdjustConfig())

    inner class AdjustLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            Timber.i("Not yet implemented")
        }

        override fun onActivityStarted(p0: Activity) {
            Timber.i("Not yet implemented")
        }

        override fun onActivityResumed(p0: Activity) {
            Adjust.onResume()
        }

        override fun onActivityPaused(p0: Activity) {
            Adjust.onPause()
        }

        override fun onActivityStopped(p0: Activity) {
            Timber.i("Not yet implemented")
        }

        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            Timber.i("Not yet implemented")
        }

        override fun onActivityDestroyed(p0: Activity) {
            Timber.i("Not yet implemented")
        }
    }
}