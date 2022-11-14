package com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.*
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher.PaymentVoucherViewModel.UIEvent.OnScheduleAutomaticPayment
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher.PaymentVoucherViewModel.UIEvent.OnSharedVoucherImage
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PaymentVoucherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val shareHelper: ShareHelper
) : BaseViewModel(true) {


    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var referenceNumber: String? = null
    var currentAmountValueString: String? = null
    var currency : String? = null
    var clientBankAccount: ClientBankAccount? = null
    var currentDate : String = ""
    var currentTime : String = ""
    var exchangeRateLabel: String? = null
    var paymentLabel: String? = null
    var shouldDisplayExchangeRate : Boolean? = null
    var isMultiCurrency : Boolean? = null
    var isAutomaticProgrammedPaymentChecked: Boolean? = false
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var identification: String? = null
    private var userName: String? = null
    private var paymentDate: String? = null



    init {
        referenceNumber = savedStateHandle[REFERENCE_NUMBER] ?: ""
        currentAmountValueString = savedStateHandle[CURRENT_AMOUNT_VALUE]
        currency = savedStateHandle[PAYMENT_LABEL]
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        userName = savedStateHandle[NAME_CLIENT] ?: ""
        clientBankAccount = savedStateHandle[CLIENT_BANK_ACCOUNT]
        paymentDate = savedStateHandle[PAYMENT_DATE]
        exchangeRateLabel = savedStateHandle[EXCHANGE_RATE_LABEL]
        paymentLabel = savedStateHandle[PAYMENT_LABEL]
        shouldDisplayExchangeRate = savedStateHandle[SHOULD_DISPLAY_EXCHANGE_RATE]
        isMultiCurrency = savedStateHandle[SHOULD_DISPLAY_EXCHANGE_RATE]
        isAutomaticProgrammedPaymentChecked = savedStateHandle[IS_AUTOMATIC_PAYMENT_CHECKED]
        val time = Calendar.getInstance().time
        currentDate = getCurrentDate(time)
        currentTime = getCurrentTime(time)
    }

    private fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        shareHelper.sharedScreenShot(view, capturingBounds)
    }

    private fun onScheduleAutomaticPayment() = navigateTo(
        route = "${Screen.PaymentScheduleScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${
        encodeData(
            clientBankAccount
        )
        }/$paymentDate/${false}/${Screen.PaymentVoucherScreen.baseRoute}/${false}"
    )

    data class UIState(
        val test: String = "",
        val showScheduleAutomaticPaymentProcess: Boolean = true,
        val accountCurrency: String = "$",
        val currentAmountValueString: String = "0",
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val enableButton: Boolean = false,
        val isAmountVisible: Boolean = true,
        val exchangeConvertedAmount: Double = 0.0,
        val isAlertResultVisible: Boolean = false,
        val alertResultTitle: String = "",
        val alertResultDescription: String = "",
        val isLoading: Boolean = false,
        )


    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnSharedVoucherImage -> onShareVoucherImage(event.view, event.capturingBounds)
            is OnScheduleAutomaticPayment -> onScheduleAutomaticPayment()
        }
    }

    sealed class UIEvent {
        data class OnSharedVoucherImage(
            val view: View,
            val capturingBounds: Rect
        ) : UIEvent()

        object OnScheduleAutomaticPayment : UIEvent()
    }
}
