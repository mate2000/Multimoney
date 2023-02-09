package com.multimoney.multimoney.presentation.ui.crypto.purchase.voucher

import android.view.View
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.accountsmart.QuerySmartAccountsUseCase
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.ShareHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BuyCryptoVoucherViewModel @Inject constructor(
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
            is UIEvent.OnCloseClick -> TODO()
            is UIEvent.OnSharedVoucherImage -> onShareVoucherImage(event.view, event.capturingBounds)
        }
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        data class OnSharedVoucherImage(
            val view: View,
            val capturingBounds: Rect
        ) : UIEvent()
    }
}
