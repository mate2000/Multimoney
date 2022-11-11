package com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.balance.Summary
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
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PaymentVoucherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val shareHelper: ShareHelper
) : BaseViewModel(true) {

    private val dateFormatter = SimpleDateFormat("dd | MM | yyyy")
    private val timeFormatter = SimpleDateFormat("hh:mm a")

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
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Int = 0
    private var summaryList: List<Summary>? = null
    private var identification: String? = null
    private var userName: String? = null
    private var paymentDate: String? = null

    init {
        referenceNumber = savedStateHandle[REFERENCE_NUMBER] ?: ""
        currentAmountValueString = savedStateHandle[CURRENT_AMOUNT_VALUE]
        currency = savedStateHandle[CURRENCY]
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        summaryList = savedStateHandle.get<Array<Summary>>(SUMMARY_LIST)?.toList()
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        userName = savedStateHandle[NAME_CLIENT] ?: ""
        clientBankAccount = savedStateHandle[CLIENT_BANK_ACCOUNT]
        paymentDate = savedStateHandle[PAYMENT_DATE]
        val time = Calendar.getInstance().time
        currentDate = dateFormatter.format(time)
        currentTime = timeFormatter.format(time)
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
        }/$paymentDate/${false}/${Screen.PaymentVoucherScreen.baseRoute}"
    )

    data class UIState(
        val test: String = "",
        val showScheduleAutomaticPaymentProcess: Boolean = true,
        val currency: String = "$",
        val currentAmountValueString: String = "0",
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val enableButton: Boolean = false,
        val isAmountVisible: Boolean = true,
        val isAutomaticProgrammedPaymentChecked: Boolean = false,
        val exchangeRateLabel: Double = 0.0,
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
