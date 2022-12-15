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
        pkUser: Int,
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
            DataEnrollDevice(pkUser.toString(), phone)
        )
    }

    fun novoEnrollPan(
        pkUser: Int,
        email: String,
        accountNumber: String,
        cardName: String,
        cardCvv: String,
        cardExpirationMonth: String,
        cardExpirationYear: String,
        onSuccessEnrollDevice: (response: NovoResponse<VtsCard>) -> Unit,
        onErrorEnrollDevice: (error: NovoError) -> Unit
    ) {
        NovoVTS.enrollPan(
            object : ResponseListener<VtsCard> {
                override fun onError(error: NovoError) {
                    onErrorEnrollDevice(error)
                }

                override fun onFinish(response: NovoResponse<VtsCard>) {
                    onSuccessEnrollDevice(response)
                }
            },
            enrollPanUserInfo = EnrollPanUserInfo(pkUser.toString(), email),
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
}
