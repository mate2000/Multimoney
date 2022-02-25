package com.multimoney.multimoney.util

import android.content.Context
import com.multimoney.multimoney.BuildConfig
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid

class SentryHelper {

    companion object {

        fun initSentry(context: Context) {
            SentryAndroid.init(context) { options ->
                options.dsn = BuildConfig.ANDROID_SENTRY_DSN
                options.environment = BuildConfig.APP_ENVIRONMENT
                options.sampleRate = 1.0
            }
        }
    }
}