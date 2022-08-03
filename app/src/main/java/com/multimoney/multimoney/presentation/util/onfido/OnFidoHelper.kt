package com.multimoney.multimoney.presentation.util.onfido

import android.content.Context
import android.content.Intent
import com.onfido.android.sdk.capture.DocumentType
import com.onfido.android.sdk.capture.OnfidoConfig
import com.onfido.android.sdk.capture.OnfidoFactory
import com.onfido.android.sdk.capture.ui.options.FlowStep
import com.onfido.android.sdk.capture.ui.options.stepbuilder.DocumentCaptureStepBuilder
import com.onfido.android.sdk.capture.utils.CountryCode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OnFidoHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // todo we have to pass the CountryCode and the document to create the customFlow
    private val nationalityIdentifyCard =
        DocumentCaptureStepBuilder.forNationalIdentity().withCountry(CountryCode.SV).build()

    private val flowStepsWithOptions: Array<FlowStep> = arrayOf(
        nationalityIdentifyCard,
        FlowStep.CAPTURE_FACE,
        FlowStep.FINAL
    )

    var onRefreshToken: (injectNewToken: (String?) -> Unit) -> Unit = {}

    private fun getOnFidoConfig(
        onFidoSDKToken: String,
    ) = OnfidoConfig.builder(context).withSDKToken(
        onFidoSDKToken,
        OnFidoExpirationHandler(onRefresh = onRefresh())
    ).withCustomFlow(flowStepsWithOptions).build()

    fun getOnFidoClient() = OnfidoFactory.create(context).client

    fun getOnFidoIntent(
        onFidoSDKToken: String,
        onRefreshToke: (injectNewToken: (String?) -> Unit) -> Unit
    ): Intent {
        onRefreshToken = onRefreshToke
        return getOnFidoClient().createIntent(
            getOnFidoConfig(onFidoSDKToken)
        )
    }

    private fun onRefresh() = object : OnFidoExpirationHandler.RefreshToken {
        override fun refreshToke(injectNewToken: (String?) -> Unit) {
            onRefreshToken.invoke(injectNewToken)
        }
    }
}