package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount

import android.content.Context
import android.view.View
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.SmartEditAmountHelper
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnCallProcessSinpeTransfer
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnTryLater
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class SmartTransferAmountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val querySmartExchangeRateUseCase: QuerySmartExchangeRateUseCase,
    private val shareHelper: ShareHelper,
    val editAmountHelper: SmartEditAmountHelper
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    var fromSmartLabel: Int = R.string.smart_iban_transfer_smart_account_colon

    private fun onStart() {
        viewModelScope.launch {
            editAmountHelper.onStart()
            fromSmartLabel = if (editAmountHelper.smartCurrency == CurrencyType.Colon) {
                R.string.smart_iban_transfer_smart_account_colon
            } else {
                R.string.smart_iban_transfer_smart_account_dolar
            }
            uiState = uiState.copy(
                currency = editAmountHelper.smartCurrency?.symbol ?: CurrencyType.Dollar.symbol,
                placeholder = if (editAmountHelper.smartCurrency == CurrencyType.Dollar) R.string.smart_dollar_placeholder else R.string.smart_colon_placeholder
            )
            if (editAmountHelper.shouldDisplayExchange) {
                getExchangeOnCompleted()
            }
        }
    }

    private fun getExchangeOnCompleted() = executeUseCase {
        editAmountHelper.getSmartExchangeRate(
            currentAmount = uiState.currentAmountValueString.toDoubleOrNull() ?: 0.0,
            onFailure = {
                onFailureWithDialog(
                    false,
                    DialogParameters(isActive = mutableStateOf(true))
                )
            },
            onLoading = {
                uiState = uiState.copy(isLoading = true)
            },
            onSuccess = { rate ->
                uiState = uiState.copy(
                    isLoading = false,
                    exchangeRate = rate?.exchangeRate ?: 0.0,
                    exchangeConvertedAmount = rate?.amount ?: 0.0,
                    exchangeRateLabel = rate?.exchangeRateLabel ?: "0.0",
                    convertedAmountLabel = rate?.convertedAmountLabel ?: "0.0"
                )
            }
        )
    }

    private fun onAmountChanged(newAmount: String) {
        uiState = uiState.copy(
            currentAmountValueString = newAmount,
            enableButton = newAmount.isNotEmpty() && newAmount.toDouble() > 0
        )
    }

    private fun onContinueClick() {
        uiState = uiState.copy(
            bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
        )
    }

    private fun onCallProcessSinpeTransfer() {
        TODO("Not yet implemented")
    }

    private fun onRetryTransfer() {
        uiState = uiState.copy(
            showErrorScreen = false,
            showLoadingScreen = true,
            paymentSuccess = false
        )
        onCallProcessSinpeTransfer()
    }

    private fun onFailureWithDialog(isLoading: Boolean, dialogParameters: DialogParameters) {
        uiState =
            uiState.copy(
                isLoading = isLoading,
                openDialog = dialogParameters
            )
    }

    private fun onNavigateToHome() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true
        )
    }

    private fun onNavigateBack() {
        navigateBack(popTo = Screen.SmartTransferIbanAccountScreen.route, isRestart = false)
    }

    data class UIState(
        val currency: String = "",
        val currentAmountValueString: String = "",
        val motive: String = "",
        val enableButton: Boolean = false,
        val isLoading: Boolean = false,
        val exchangeRate: Double = 0.0,
        val exchangeConvertedAmount: Double = 0.0,
        val exchangeRateLabel: String = "0.0",
        val convertedAmountLabel: String = "0.0",
        val placeholder: Int = R.string.smart_dollar_placeholder,
        val openDialog: DialogParameters = DialogParameters(),
        val idCard: Long = 0,
        val bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
        val cardBankName: String = "",
        var showErrorScreen: Boolean = false,
        val showLoadingScreen: Boolean = false,
        val paymentSuccess: Boolean = false,
        val referenceNumber: String = "",
        var currentDate: String = "",
        var currentTime: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnStart -> onStart()
            is OnAmountValueChange -> onAmountChanged(uiEvent.value)
            is UIEvent.OnAmountCompleted -> getExchangeOnCompleted()
            is UIEvent.OnMotiveChange -> uiState = uiState.copy(motive = uiEvent.value)
            is OnContinueClick -> onContinueClick()
            is OnCallProcessSinpeTransfer -> onCallProcessSinpeTransfer()
            is OnFailureWithDialog -> onFailureWithDialog(
                uiEvent.isLoading,
                uiEvent.dialogParameters
            )
            is OnRetryTransfer -> onRetryTransfer()
            is OnTryLater -> editAmountHelper.onTryLater(
                uiEvent.notificationTitle,
                uiEvent.notificationBody,
                uiEvent.notificationSmallIcon,
                uiEvent.context
            ) { onNavigateToHome() }
            is OnNavigateHome -> onNavigateToHome()
            is OnShareVoucherImage -> editAmountHelper.onShareVoucherImage(
                uiEvent.view,
                uiEvent.capturingBounds
            )
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnStart : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
        data class OnAmountCompleted(val value: String) : UIEvent()
        data class OnMotiveChange(val value: String) : UIEvent()
        object OnContinueClick : UIEvent()
        object OnCallProcessSinpeTransfer : UIEvent()
        object OnRetryTransfer : UIEvent()
        data class OnFailureWithDialog(
            val isLoading: Boolean,
            val dialogParameters: DialogParameters
        ) : UIEvent()

        data class OnTryLater(
            val notificationTitle: String,
            val notificationBody: String,
            val notificationSmallIcon: Int,
            val context: Context
        ) : UIEvent()

        object OnNavigateHome : UIEvent()
        data class OnShareVoucherImage(
            val view: View,
            val capturingBounds: Rect
        ) : UIEvent()
    }
}