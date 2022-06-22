package com.multimoney.multimoney.presentation.util.onfido

import android.content.Context
import com.multimoney.domain.interaction.security.MutationOnFidoInitialProcessUseCase
import com.onfido.android.sdk.capture.DocumentType
import com.onfido.android.sdk.capture.OnfidoConfig
import com.onfido.android.sdk.capture.OnfidoFactory
import com.onfido.android.sdk.capture.ui.options.FlowStep
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OnFidoHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    val mutationOnFidoInitialProcessUseCase: MutationOnFidoInitialProcessUseCase
) {

    private val flowStepsWithOptions: Array<FlowStep> = arrayOf(
        FlowStep.CAPTURE_DOCUMENT,
        FlowStep.CAPTURE_FACE,
        FlowStep.FINAL
    )

    private val onFidoDocuments: ArrayList<DocumentType> =
        arrayListOf(DocumentType.NATIONAL_IDENTITY_CARD)

    private fun getOnFidoConfig(
        onFidoSDKToken: String,
    ) = OnfidoConfig.builder(context).withSDKToken(
        onFidoSDKToken,
        OnFidoExpirationHandler(mutationOnFidoInitialProcessUseCase)
    ).withCustomFlow(flowStepsWithOptions).withAllowedDocumentTypes(onFidoDocuments).build()

    fun getOnFidoClient() = OnfidoFactory.create(context).client

    fun getOnFidoIntent(
        onFidoSDKToken: String,
    ) = getOnFidoClient().createIntent(
        getOnFidoConfig(onFidoSDKToken)
    )
}