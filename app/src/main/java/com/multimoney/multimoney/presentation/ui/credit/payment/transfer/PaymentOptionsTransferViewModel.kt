package com.multimoney.multimoney.presentation.ui.credit.payment.transfer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.security.TransferAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.Screen.HomeScreen
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.TRANSFER_ACCOUNT
import com.multimoney.multimoney.presentation.ui.credit.payment.transfer.PaymentOptionsTransferViewModel.BaseEvent.OnCopyTextToClipboardEvent
import com.multimoney.multimoney.presentation.ui.credit.payment.transfer.PaymentOptionsTransferViewModel.UIEvent.OnCopyTextToClipboard
import com.multimoney.multimoney.presentation.ui.credit.payment.transfer.PaymentOptionsTransferViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.transfer.PaymentOptionsTransferViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentOptionsTransferViewModel @Inject constructor(savedStateHandle: SavedStateHandle) :
    BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var idBrand: Int = 0

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        uiState = uiState.copy(
            creditNumber = savedStateHandle[CREDIT_NUMBER],
            transferAccount = savedStateHandle[TRANSFER_ACCOUNT],
            isAccountNumberVisible = idBrand == Brand.ElSalvador.id
        )
        onGetTextResource()
    }

    private fun onGetTextResource() {
        uiState = when (idBrand) {
            Brand.Mexico.id -> {
                uiState.copy(
                    titleResource = string.payment_options_transfer_title_mx,
                    disclaimerResource = string.payment_options_transfer_disclaimer_mx
                )
            }
            else -> uiState.copy(
                titleResource = string.payment_options_transfer_title_sv,
                disclaimerResource = string.payment_options_transfer_disclaimer
            )
        }
    }

    private fun onCopyTextToClipboard(text: String) {
        emitBaseEvent(OnCopyTextToClipboardEvent(text))
    }

    private fun onNavigateBackHome(showDialog: Boolean) {
        if (showDialog) {
            uiState = uiState.copy(
                openDialog = DialogParameters(
                    titleResource = string.payment_options_transfer_close_dialog_title,
                    descriptionResource = string.payment_options_transfer_close_dialog_description,
                    negativeResource = string.credit_close_dialog_negative_button_text,
                    positiveAction = {
                        navigateBack(popTo = HomeScreen.route, isRestart = false)
                    },
                    isActive = mutableStateOf(true)
                )
            )
        } else {
            navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)
        }
    }

    private fun onNavigateBack() {
        when (idBrand) {
            Brand.Mexico.id -> navigateBack(popTo = HomeScreen.route, isRestart = false)
            else -> navigateBack(popTo = Screen.PaymentOptionsScreen.route, isRestart = false)
        }
    }

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val disclaimerResource: Int = R.string.empty,
        val creditNumber: String? = null,
        val transferAccount: TransferAccount? = null,
        val isAccountNumberVisible: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateBackHome -> onNavigateBackHome(uiEvent.showDialog)
            is OnCopyTextToClipboard -> onCopyTextToClipboard(uiEvent.text)
        }
    }

    sealed class UIEvent {
        class OnCopyTextToClipboard(val text: String) : UIEvent()
        object OnNavigateBack : UIEvent()
        class OnNavigateBackHome(val showDialog: Boolean) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnCopyTextToClipboardEvent(val text: String) : BaseEvent()
    }
}
