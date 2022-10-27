package com.multimoney.multimoney.presentation.ui.payment.paymentvoucher

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.payment.paymentvoucher.PaymentVoucherViewModel.UIEvent.OnScheduleAutomaticPayment
import com.multimoney.multimoney.presentation.ui.payment.paymentvoucher.PaymentVoucherViewModel.UIEvent.OnSharedVoucherImage
import com.multimoney.multimoney.presentation.util.SharedHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentVoucherViewModel @Inject constructor(
    val sharedHelper: SharedHelper
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // stateLess

    data class UIState(
        val test: String = "",
        val showScheduleAutomaticPaymentProcess: Boolean = true
    )

    private fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        sharedHelper.sharedScreenShot(view, capturingBounds)
    }

    private fun onScheduleAutomaticPayment() {
    }

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
