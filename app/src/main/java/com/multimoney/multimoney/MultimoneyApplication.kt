package com.multimoney.multimoney

import android.app.Application
import com.multimoney.multimoney.util.SentryHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
open class MultimoneyApplication : Application() {

    @Inject
    lateinit var sentryHelper: SentryHelper

    override fun onCreate() {
        super.onCreate()
        initThirdPartySdks()
    }

    open fun initThirdPartySdks() {
        sentryHelper.initSentry()
    }
}
