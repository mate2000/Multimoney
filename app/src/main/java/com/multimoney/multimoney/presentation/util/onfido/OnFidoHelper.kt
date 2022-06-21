package com.multimoney.multimoney.presentation.util.onfido

import android.content.Context
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.onfido.android.sdk.capture.DocumentType
import com.onfido.android.sdk.capture.OnfidoConfig
import com.onfido.android.sdk.capture.OnfidoFactory
import com.onfido.android.sdk.capture.token.TokenExpirationHandler
import com.onfido.android.sdk.capture.ui.options.FlowStep
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnFidoHelper @Inject constructor(@ApplicationContext private val context: Context) {

    private val flowStepsWithOptions: Array<FlowStep> = arrayOf(
        FlowStep.CAPTURE_DOCUMENT,
        FlowStep.CAPTURE_FACE,
        FlowStep.FINAL
    )

    private val onFidoDocuments: ArrayList<DocumentType> =
        arrayListOf(DocumentType.NATIONAL_IDENTITY_CARD)

    private fun getOnFidoConfig(
        onFidoSDKToken: String,
        tokenExpirationHandler: TokenExpirationHandler
    ) = OnfidoConfig.builder(context)
        .withSDKToken(
            onFidoSDKToken,
            tokenExpirationHandler = tokenExpirationHandler
        )
        .withCustomFlow(flowStepsWithOptions).withAllowedDocumentTypes(onFidoDocuments).build()

    fun getOnFidoClient() = OnfidoFactory.create(context).client

    fun getOnFidoIntent(onFidoSDKToken: String, tokenExpirationHandler: TokenExpirationHandler) =
        getOnFidoClient().createIntent(getOnFidoConfig(onFidoSDKToken, tokenExpirationHandler))
}