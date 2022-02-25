package com.multimoney.multimoney

import android.app.Application
import com.multimoney.multimoney.util.SentryHelper
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
open class MultimoneyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initThirdPartySdks()
    }

    open fun initThirdPartySdks() {
        SentryHelper.initSentry(this)
    }
}
