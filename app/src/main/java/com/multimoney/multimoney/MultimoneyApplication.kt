package com.multimoney.multimoney

import android.app.Application
import com.multimoney.multimoney.presentation.util.NfcHelper
import androidx.work.Configuration
import com.multimoney.multimoney.presentation.ui.credit.movements.workmanager.DownloadCreditMovementsWorkerProvider
import com.multimoney.multimoney.util.AdjustHelper
import com.multimoney.multimoney.util.CognitoHelper
import com.multimoney.multimoney.util.SentryHelper
import com.novopayment.sdk.vts.NovoVTS
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class MultimoneyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var sentryHelper: SentryHelper

    @Inject
    lateinit var adjustHelper: AdjustHelper

    @Inject
    lateinit var cognitoHelper: CognitoHelper

    @Inject
    lateinit var nfcHelper: NfcHelper

    @Inject
    lateinit var downloadCreditMovementsWorkerProvider: DownloadCreditMovementsWorkerProvider

    override fun onCreate() {
        super.onCreate()
        initThirdPartySdks()

        // Initialize Adjust callback
        registerActivityLifecycleCallbacks(adjustHelper.AdjustLifecycleCallbacks())
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(downloadCreditMovementsWorkerProvider)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    open fun initThirdPartySdks() {
        sentryHelper.initSentry()
        adjustHelper.initAdjust()
        cognitoHelper.initCognito()
        if (nfcHelper.isNfcSupported()) {
            NovoVTS.isInitialized()
        }
    }
}
