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
            "eyJhbGciOiJFUzUxMiJ9.eyJleHAiOjE2NTM5NTAyNzEsInBheWxvYWQiOnsiYXBwIjoiYjBiMmM2NDUtMGJkMi00Zjk0LWI1M2EtNGRmYWQ1Yzk2NzhhIiwiY2xpZW50X3V1aWQiOiIzNjIzYjNhZC1mNDc3LTQwYjUtYTc2Zi04MDEyZWI3NzIyZDciLCJpc19zYW5kYm94Ijp0cnVlLCJzYXJkaW5lX3Nlc3Npb24iOiIxZmE4MDU2Mi0zYTkxLTRlMDUtOTkzYS0yMWZmM2M4M2NhMGIifSwidXVpZCI6IkIzc21vT0M5eGlMIiwidXJscyI6eyJ0ZWxlcGhvbnlfdXJsIjoiaHR0cHM6Ly90ZWxlcGhvbnkub25maWRvLmNvbSIsImRldGVjdF9kb2N1bWVudF91cmwiOiJodHRwczovL3Nkay5vbmZpZG8uY29tIiwic3luY191cmwiOiJodHRwczovL3N5bmMub25maWRvLmNvbSIsImhvc3RlZF9zZGtfdXJsIjoiaHR0cHM6Ly9pZC5vbmZpZG8uY29tIiwiYXV0aF91cmwiOiJodHRwczovL2FwaS5vbmZpZG8uY29tIiwib25maWRvX2FwaV91cmwiOiJodHRwczovL2FwaS5vbmZpZG8uY29tIn19.MIGIAkIB3J6TIw8uBZF8JDrdT_cayy5U-pdv4IKQsRRvCWk2vw3PVgAZsJoAjBPQQE1wQYkxpxpukPA0Ql8Jn0WM1mIvDq8CQgF7WlSuJ0Crke1-qWsC7CV63wjZzKSKBzEwPOnTAEGs-JX22L9A91T4FMa8Vfde1YtS2cPwG3khQ9c3XZFFhgnTQg"
    }
}