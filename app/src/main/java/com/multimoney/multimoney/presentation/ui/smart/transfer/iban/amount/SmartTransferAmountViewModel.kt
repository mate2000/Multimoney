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
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.IBAN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnCallProcessSinpeTransfer
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnTryLater
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.workers.startTimedNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class, FlowPreview::class)
class SmartTransferAmountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val querySmartExchangeRateUseCase: QuerySmartExchangeRateUseCase,
    private val shareHelper: ShareHelper,
) : BaseViewModel(true) {

    var uiState by mutableStateOf(SavingAmountViewModel.UIState())
        private set

    // stateless
    private var idBrand: Int = 0
    private var identification: String = ""
    private var user: String = ""
    private var idCurrency: Int = 0
    private var smartAccount: SmartAccountID? = null
    private var ibanAccount: IbanAccountID? = null
    var smartCurrency: CurrencyType? = CurrencyType.Dollar
    var ibanCurrency: CurrencyType? = null
    var shouldDisplayExchange: Boolean = false

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        smartAccount = savedStateHandle[SMART_IDS]
        ibanAccount = savedStateHandle[IBAN_ACCOUNT]
        smartCurrency = smartAccount?.currencyID?.getCurrencyFromId()
        ibanCurrency = ibanAccount?.currencyId?.getCurrencyFromId()
        shouldDisplayExchange = smartCurrency == ibanCurrency
        identification = savedStateHandle[IDENTIFICATION] ?: ""
    }

    private fun getSmartExchangeRate(
        user: String,
        identification: String,
        idOriginCurrency: String,
        idDestinationCurrency: String
    ) = executeUseCase {
        uiState.currentAmountValueString.debounce(SavingAmountViewModel.TWO_SECONDS).collectLatest {
            querySmartExchangeRateUseCase.invoke(
                user = user,
                idBrand = idBrand,
                abbreviation = ibanCurrency?.disbursementValue ?: "",
                identification = identification,
                idOriginCurrency = idOriginCurrency,
                idDestinationCurrency = idDestinationCurrency,
                amount = it?.toDoubleOrNull() ?: 0.0
            ).collectLatest { result ->
                result.onSuccess { rate ->
                    uiState = uiState.copy(
                        isLoading = false,
                        exchangeRate = rate?.exchangeRate ?: 0.0,
                        exchangeConvertedAmount = rate?.amount ?: 0.0,
                        exchangeRateLabel = rate?.exchangeRateLabel ?: "0.0",
                        convertedAmountLabel = rate?.convertedAmountLabel ?: "0.0"
                    )
                }
                result.onFailure {
                    onUIEvent(
                        OnFailureWithDialog(
                            isLoading = false,
                            dialogParameters = DialogParameters(isActive = mutableStateOf(true))
                        )
                    )
                }
                result.onLoading { uiState = uiState.copy(isLoading = true) }
            }
        }
    }

    private fun onAmountChanged(newAmount: String) {
        uiState.currentAmountValueString.value = newAmount
        uiState = uiState.copy(
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

    private fun onTryLater(
        notificationTitle: String,
        notificationBody: String,
        notificationSmallIcon: Int,
        context: Context
    ) {
        startTimedNotification(
            context,
            notificationTitle,
            notificationBody,
            notificationSmallIcon
        )
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true
        )
    }

    private fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        shareHelper.sharedScreenShot(view, capturingBounds)
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
        navigateBack(popTo = Screen.TransferIbanAccountScreen.route, isRestart = false)
    }

    data class UIState(
        // Interactions
        val currency: String = "",
        val motive: String = "",
        val currentAmountValueString: MutableStateFlow<String?> = MutableStateFlow(null),
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
            is OnAmountValueChange -> onAmountChanged(uiEvent.value)
            is OnContinueClick -> onContinueClick()
            is OnCallProcessSinpeTransfer -> onCallProcessSinpeTransfer()
            is OnFailureWithDialog -> onFailureWithDialog(
                uiEvent.isLoading,
                uiEvent.dialogParameters
            )
            is OnRetryTransfer -> onRetryTransfer()
            is OnTryLater -> onTryLater(
                uiEvent.notificationTitle,
                uiEvent.notificationBody,
                uiEvent.notificationSmallIcon,
                uiEvent.context
            )
            is OnNavigateHome -> onNavigateToHome()
            is OnShareVoucherImage -> onShareVoucherImage(uiEvent.view, uiEvent.capturingBounds)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
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