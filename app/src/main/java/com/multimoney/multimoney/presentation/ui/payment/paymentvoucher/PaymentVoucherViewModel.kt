package com.multimoney.multimoney.presentation.ui.payment.paymentvoucher

import com.multimoney.multimoney.presentation.base.BaseViewModel

class PaymentVoucherViewModel : BaseViewModel(true) {

    sealed class UIEvent {
        object OnSharedVoucherImage : UIEvent()
    }
}
