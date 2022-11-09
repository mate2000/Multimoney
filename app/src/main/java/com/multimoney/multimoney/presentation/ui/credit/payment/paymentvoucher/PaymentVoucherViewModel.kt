package com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher

import android.view.View
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.REFERENCE_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
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
    var summaryList: List<Summary>? = null
    var clientBankAccount: ClientBankAccount? = null
    var currentDate : String = ""
    var currentTime : String = ""




    init {
        referenceNumber = savedStateHandle[REFERENCE_NUMBER] ?: ""
        clientBankAccount = savedStateHandle[CLIENT_BANK_ACCOUNT]
        val time = Calendar.getInstance().time
        currentDate = dateFormatter.format(time)
        currentTime = timeFormatter.format(time)

    }

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
        val clientBankAccount: ClientBankAccount? = null,
        val isAlertResultVisible: Boolean = false,
        val alertResultTitle: String = "",
        val alertResultDescription: String = "",
        val isLoading: Boolean = false,

    )

    private fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        shareHelper.sharedScreenShot(view, capturingBounds)
    }

    private fun onScheduleAutomaticPayment() {
    }
    fun shouldDisplayExchangeRate() =
        isMultiCurrency() ||
                uiState.clientBankAccount?.idCurrency?.toString() != summaryList?.first()?.idCurrency?.toString()

    fun isMultiCurrency() = (summaryList?.count() ?: 1) > 1

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
