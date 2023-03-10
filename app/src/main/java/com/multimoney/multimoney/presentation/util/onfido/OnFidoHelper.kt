package com.multimoney.multimoney.presentation.util.onfido

import android.content.Context
import android.content.Intent
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.presentation.util.catalog.AppFlow
import com.onfido.android.sdk.capture.DocumentType
import com.onfido.android.sdk.capture.DocumentType.DRIVING_LICENCE
import com.onfido.android.sdk.capture.DocumentType.NATIONAL_IDENTITY_CARD
import com.onfido.android.sdk.capture.DocumentType.PASSPORT
import com.onfido.android.sdk.capture.OnfidoConfig
import com.onfido.android.sdk.capture.OnfidoFactory
import com.onfido.android.sdk.capture.ui.options.FlowStep
import com.onfido.android.sdk.capture.ui.options.stepbuilder.DocumentCaptureStepBuilder
import com.onfido.android.sdk.capture.utils.CountryCode
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

class OnFidoHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    var onRefreshToken: (injectNewToken: (String?) -> Unit) -> Unit = {}

    private fun getOnFidoConfigForNationalIdentity(
        idBrand: Int?,
        onFidoSDKToken: String
    ) = OnfidoConfig.builder(context).withSDKToken(
        onFidoSDKToken,
        OnFidoExpirationHandler(onRefresh = onRefresh())
    ).withCustomFlow(
        createFlowStepOptions(
            DocumentCaptureStepBuilder.forNationalIdentity().withCountry(getCountryCode(idBrand)).build()
        )
    ).withLocale(Locale.forLanguageTag(CR_LANGUAGE_TAG)).build()

    private fun getOnFidoConfigForSeveralDocuments(
        idBrand: Int?,
        documentsAccept: List<DocumentType>,
        onFidoSDKToken: String
    ) = OnfidoConfig.builder(context).withSDKToken(
        onFidoSDKToken,
        OnFidoExpirationHandler(onRefresh = onRefresh())
    ).withAllowedDocumentTypes(documentsAccept).withCustomFlow(
        createFlowStepOptions(
            DocumentCaptureStepBuilder.forGenericDocument().withCountry(getCountryCode(idBrand)).build()
        )
    ).build()

    fun getOnFidoClient() = OnfidoFactory.create(context).client

    fun getOnFidoIntent(
        idBrand: Int?,
        appFlow: AppFlow,
        onFidoSDKToken: String,
        onRefreshToke: (injectNewToken: (String?) -> Unit) -> Unit
    ): Intent {
        onRefreshToken = onRefreshToke
        return getOnFidoClient().createIntent(
            when (appFlow) {
                AppFlow.CREDIT_ORIGINATION -> {
                    getOnFidoConfigForNationalIdentity(idBrand, onFidoSDKToken)
                }
                AppFlow.SIGN_OUT -> {
                    getOnFidoConfigForSeveralDocuments(
                        idBrand,
                        listOf(NATIONAL_IDENTITY_CARD, PASSPORT, DRIVING_LICENCE),
                        onFidoSDKToken
                    )
                }
                AppFlow.SMART -> {
                    getOnFidoConfigForNationalIdentity(idBrand, onFidoSDKToken)
                }
            }
        )
    }

    private fun onRefresh() = object : OnFidoExpirationHandler.RefreshToken {
        override fun refreshToke(injectNewToken: (String?) -> Unit) {
            onRefreshToken.invoke(injectNewToken)
        }
    }

    private fun getCountryCode(idBrand: Int?): CountryCode {
        return when (idBrand) {
            Brand.ElSalvador.id -> CountryCode.SV
            Brand.Guatemala.id -> CountryCode.GT
            Brand.CostaRica.id -> CountryCode.CR
            else -> CountryCode.CR
        }
    }

    private fun createFlowStepOptions(stepFlow: FlowStep): Array<FlowStep> {
        return arrayOf(
            stepFlow,
            FlowStep.CAPTURE_FACE,
            FlowStep.FINAL
        )
    }
    companion object {
        const val CR_LANGUAGE_TAG = "es-CR"
    }
}
