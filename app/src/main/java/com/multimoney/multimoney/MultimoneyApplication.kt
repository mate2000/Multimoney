package com.multimoney.multimoney

import android.app.Application
import com.multimoney.multimoney.util.AdjustHelper
import com.multimoney.multimoney.util.SentryHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
open class MultimoneyApplication : Application() {

    @Inject
    lateinit var sentryHelper: SentryHelper

    @Inject
    lateinit var adjustHelper: AdjustHelper

    override fun onCreate() {
        super.onCreate()
        initThirdPartySdks()

        // Initialize Adjust callbak
        registerActivityLifecycleCallbacks(adjustHelper.AdjustLifecycleCallbacks())
    }

    open fun initThirdPartySdks() {
        sentryHelper.initSentry()
        adjustHelper.initAdjust()
    }
}
