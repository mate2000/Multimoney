package com.multimoney.multimoney.presentation.util.onfido

import android.content.Context
import com.onfido.android.sdk.capture.DocumentType
import com.onfido.android.sdk.capture.OnfidoConfig
import com.onfido.android.sdk.capture.OnfidoFactory
import com.onfido.android.sdk.capture.ui.options.FlowStep
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OnFidoHelper @Inject constructor(@ApplicationContext private val context: Context) {

    private val flowStepsWithOptions: Array<FlowStep> = arrayOf(
        FlowStep.CAPTURE_DOCUMENT,
        FlowStep.CAPTURE_FACE,
        FlowStep.FINAL
    )

    private val onFidoDocuments: ArrayList<DocumentType> =
        arrayListOf(DocumentType.NATIONAL_IDENTITY_CARD)

    // TODO: SDK Token is hardcoded we have to implement the logic to get it from API
    private fun getOnFidoConfig() =
        OnfidoConfig.builder(context)
            .withSDKToken(SDK_TOKEN, tokenExpirationHandler = OnFidoExpirationHandler())
            .withCustomFlow(flowStepsWithOptions).withAllowedDocumentTypes(onFidoDocuments).build()

    fun getOnFidoClient() = OnfidoFactory.create(context).client

    fun getOnFidoIntent() = getOnFidoClient().createIntent(getOnFidoConfig())

    companion object {
        // TODO: This token expires every 90 minutes so we have to implement the api call to get this sdk token
        const val SDK_TOKEN =
            "eyJhbGciOiJFUzUxMiJ9.eyJleHAiOjE2NTI5Nzk1MDksInBheWxvYWQiOnsiYXBwIjoiYjBiMmM2NDUtMGJkMi00Zjk0LWI1M2EtNGRmYWQ1Yzk2NzhhIiwiY2xpZW50X3V1aWQiOiIzNjIzYjNhZC1mNDc3LTQwYjUtYTc2Zi04MDEyZWI3NzIyZDciLCJpc19zYW5kYm94Ijp0cnVlLCJzYXJkaW5lX3Nlc3Npb24iOiI2ZTFhNGExMi0zY2Q4LTRiNjUtYTE0ZS1jMGNiNTg2ODE3OTIifSwidXVpZCI6IkIzc21vT0M5eGlMIiwidXJscyI6eyJ0ZWxlcGhvbnlfdXJsIjoiaHR0cHM6Ly90ZWxlcGhvbnkub25maWRvLmNvbSIsImRldGVjdF9kb2N1bWVudF91cmwiOiJodHRwczovL3Nkay5vbmZpZG8uY29tIiwic3luY191cmwiOiJodHRwczovL3N5bmMub25maWRvLmNvbSIsImhvc3RlZF9zZGtfdXJsIjoiaHR0cHM6Ly9pZC5vbmZpZG8uY29tIiwiYXV0aF91cmwiOiJodHRwczovL2FwaS5vbmZpZG8uY29tIiwib25maWRvX2FwaV91cmwiOiJodHRwczovL2FwaS5vbmZpZG8uY29tIn19.MIGIAkIB-_Fte-BHNz_4WPswpqHH7KV7ILTJtQpfjU-8h9OP1j4z3SFp8TL_Y8bTQGgjPxjgsIVAj2P2T9uerjpruxEThv8CQgHW6pdVZtkjI5QPKOfaIYCMHRNR3nysATp9x5YhuK4F_XtPcWjRUtt7_cZhcBD-rnsbxH9cIeEFfLTCC37Z8DR_Pg"
    }
}