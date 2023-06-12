package com.multimoney.multimoney.presentation.ui.crypto.send.voucher

import android.view.View
import androidx.compose.ui.geometry.Rect
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.ShareHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SendCryptoVoucherViewModel @Inject constructor(
    private val shareHelper: ShareHelper
) : BaseViewModel(true) {

    private fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        shareHelper.sharedScreenShot(view, capturingBounds)
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnSharedVoucherImage -> onShareVoucherImage(event.view, event.capturingBounds)
        }
    }

    sealed class UIEvent {
        data class OnSharedVoucherImage(
            val view: View,
            val capturingBounds: Rect
        ) : UIEvent()
    }
}