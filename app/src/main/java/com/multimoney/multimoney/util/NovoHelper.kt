package com.multimoney.multimoney.util

import com.multimoney.multimoney.BuildConfig
import com.novopayment.sdk.vts.NovoVTS
import com.novopayment.sdk.vts.NovoVTS.ResponseListener
import com.novopayment.sdk.vts.model.DataEnrollDevice
import com.novopayment.sdk.vts.model.EnrollFlow.GREEN
import com.novopayment.sdk.vts.model.ExpirationDate
import com.novopayment.sdk.vts.model.NovoError
import com.novopayment.sdk.vts.model.NovoResponse
import com.novopayment.sdk.vts.model.VtsCard
import com.novopayment.sdk.vts.network.request.EnrollPanData
import com.novopayment.sdk.vts.network.request.EnrollPanUserInfo
import javax.inject.Inject

class NovoHelper @Inject constructor() {

    fun configureNovoSdk() {
        if (NovoVTS.isConfiguredForVts().not()) {
            NovoVTS.setConfigurations(
                BuildConfig.NOVO_CLIENT_ID,
                BuildConfig.NOVO_API_KEY,
                BuildConfig.NOVO_API_URL,
                BuildConfig.NOVO_VCEH_URL,
                BuildConfig.NOVO_SOCKET_URL
            )
        }
    }

    fun novoEnrollDevice(
        identification: String,
        phone: String,
        onSuccessEnrollDevice: (response: NovoResponse<String>) -> Unit,
        onErrorEnrollDevice: (error: NovoError) -> Unit
    ) {
        NovoVTS.enrollDevice(
            object : ResponseListener<String> {
                override fun onError(error: NovoError) {
                    onErrorEnrollDevice(error)
                }

                override fun onFinish(response: NovoResponse<String>) {
                    onSuccessEnrollDevice(response)
                }
            },
            DataEnrollDevice(identification, phone)
        )
    }

    fun novoEnrollPan(
        identification: String,
        email: String,
        accountNumber: String,
        cardName: String,
        cardCvv: String,
        cardExpirationMonth: String,
        cardExpirationYear: String,
        onSuccessEnrollPan: (response: NovoResponse<VtsCard>) -> Unit,
        onErrorEnrollPan: (error: NovoError) -> Unit
    ) {
        NovoVTS.enrollPan(
            object : ResponseListener<VtsCard> {
                override fun onError(error: NovoError) {
                    onErrorEnrollPan(error)
                }

                override fun onFinish(response: NovoResponse<VtsCard>) {
                    onSuccessEnrollPan(response)
                }
            },
            enrollPanUserInfo = EnrollPanUserInfo(identification, email),
            enrollPanData = EnrollPanData(
                accountNumber,
                cardName,
                cardCvv,
                ExpirationDate(cardExpirationMonth, cardExpirationYear)
            ),
            enrollFlow = GREEN,
            withTermsAndConditions = false
        )
    }

    fun novoNewPayment(
        vProvisionedTokenId: String,
        onSuccessPayment: (response: NovoResponse<Boolean>) -> Unit,
        onErrorPayment: (error: NovoError) -> Unit
    ) {
        NovoVTS.newPayment(
            listener = object : ResponseListener<Boolean> {
                override fun onError(error: NovoError) {
                    onErrorPayment(error)
                }

                override fun onFinish(response: NovoResponse<Boolean>) {
                    onSuccessPayment(response)
                }
            },
            vProvisionedTokenId
        )
    }
}
