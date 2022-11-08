package com.multimoney.multimoney.presentation.util.onfido

import android.content.Context
import android.content.Intent
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.presentation.util.catalog.AppFlow
import com.multimoney.multimoney.presentation.util.catalog.AppFlow.CreditOriginationFlow
import com.multimoney.multimoney.presentation.util.catalog.AppFlow.SignUpFlow
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
    var onRefreshToken: (injectNewToken: (String?) -> Unit) -> Unit = {}

    private fun getOnFidoConfig(
        idBrand: Int,
        appFlow: AppFlow,
        onFidoSDKToken: String
    ) = OnfidoConfig.builder(context).withSDKToken(
        onFidoSDKToken,
        OnFidoExpirationHandler(onRefresh = onRefresh())
    ).withCustomFlow(createFlowStepOptions(idBrand, appFlow)).build()

    fun getOnFidoClient() = OnfidoFactory.create(context).client

    fun getOnFidoIntent(
        idBrand: Int,
        appFlow: AppFlow,
        onFidoSDKToken: String,
        onRefreshToke: (injectNewToken: (String?) -> Unit) -> Unit
    ): Intent {
        onRefreshToken = onRefreshToke
        return getOnFidoClient().createIntent(
            getOnFidoConfig(idBrand, appFlow, onFidoSDKToken)
        )
    }

    private fun onRefresh() = object : OnFidoExpirationHandler.RefreshToken {
        override fun refreshToke(injectNewToken: (String?) -> Unit) {
            onRefreshToken.invoke(injectNewToken)
        }
    }

    private fun getConfigurationByCountryAndFlow(idBrand: Int, appFlow: AppFlow): FlowStep {
        val countryCode = when (idBrand) {
            Brand.ElSalvador.id -> CountryCode.SV
            Brand.Guatemala.id -> CountryCode.GT
            Brand.CostaRica.id -> CountryCode.CR
            else -> CountryCode.CR
        }
        return when (appFlow) {
            is CreditOriginationFlow -> {
                DocumentCaptureStepBuilder.forNationalIdentity().withCountry(countryCode).build()
            }
            is SignUpFlow -> {
                DocumentCaptureStepBuilder.forGenericDocument().build()
            }
        }
    }

    private fun createFlowStepOptions(idBrand: Int, appFlow: AppFlow): Array<FlowStep> {
        return arrayOf(
            getConfigurationByCountryAndFlow(idBrand, appFlow),
            FlowStep.CAPTURE_FACE,
            FlowStep.FINAL
        )
    }
}
