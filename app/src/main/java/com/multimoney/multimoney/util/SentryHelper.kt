package com.multimoney.multimoney.util

import android.content.Context
import com.multimoney.multimoney.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import io.sentry.android.core.SentryAndroid
import javax.inject.Inject

class SentryHelper @Inject constructor(@ApplicationContext private val context: Context) {

    fun initSentry() {
        SentryAndroid.init(context) { options ->
            options.dsn = BuildConfig.SENTRY_DSN
            options.environment = BuildConfig.APP_ENVIRONMENT
            options.sampleRate = 1.0
        }
    }
}