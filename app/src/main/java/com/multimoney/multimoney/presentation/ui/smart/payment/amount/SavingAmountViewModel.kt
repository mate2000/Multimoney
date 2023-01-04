package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import android.content.Context
import android.view.View
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationProcessSinpeTransferUseCase
import com.multimoney.domain.interaction.accountsmart.MutationProcessTransferVisaToSmartVDUseCase
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel.Companion.CURRENCY_SEPARATOR
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnCallProcessTransfer
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnSuggestedAmountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnTryLater
import com.multimoney.multimoney.presentation.util.SmartEditAmountHelper
import com.multimoney.multimoney.presentation.util.SmartEditAmountHelper.Companion.DEFAULT_DESCRIPTION
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SuggestedAmount
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder
import com.multimoney.multimoney.presentation.util.formattedTwoDecimalsNumber
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import com.multimoney.multimoney.presentation.util.stringToDoubleFormat
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class SavingAmountViewModel @Inject constructor(
    val editAmountHelper: SmartEditAmountHelper,
    private val processTransferVisaToSmart: MutationProcessTransferVisaToSmartVDUseCase,
    private val processSinpeTransferUseCase: MutationProcessSinpeTransferUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    private fun onStart() {
        viewModelScope.launch {
            editAmountHelper.onStart()
            uiState = uiState.copy(
                currency = editAmountHelper.smartCurrency?.symbol ?: Dollar.symbol,
                placeholder = if (editAmountHelper.smartCurrency == Dollar) {
                    R.string.smart_dollar_placeholder
                } else {
                    R.string.smart_colon_placeholder
                },
                minSuggestion = SuggestedAmount.createSuggestion(
                    editAmountHelper.smartCurrency == Dollar,
                    SuggestionOrder.MIN
                ),
                mediumSuggestion = SuggestedAmount.createSuggestion(
                    editAmountHelper.smartCurrency == Dollar,
                    SuggestionOrder.MEDIUM
                ),
                maxSuggestion = SuggestedAmount.createSuggestion(
                    editAmountHelper.smartCurrency == Dollar,
                    SuggestionOrder.MAX
                )
            )
            getExchangeOnCompleted()
        }
    }

    private fun getExchangeOnCompleted() {
        if (editAmountHelper.shouldDisplayExchange) {
            executeUseCase {
                editAmountHelper.getSmartExchangeRate(
                    currentAmount = uiState.currentAmountValueString?.toDoubleOrNull() ?: 0.0,
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
        }
    }

    private fun onProcessTransfer() {
        if (editAmountHelper.idBrand == Brand.CostaRica.id) {
            onCallProcessSinpeTransfer()
        } else if (editAmountHelper.idBrand == Brand.ElSalvador.id) {
            onCallProcessTransferVisaToSmart()
        }
    }

    private fun onCallProcessTransferVisaToSmart() {
        executeUseCase {
            processTransferVisaToSmart.invoke(
                editAmountHelper.idCard,
                editAmountHelper.tokenNumber,
                editAmountHelper.identification,
                uiState.currentAmountValueString ?: "",
                editAmountHelper.idCurrency,
                DEFAULT_DESCRIPTION,
                editAmountHelper.maskedCardNumber,
                editAmountHelper.pkUser,
                editAmountHelper.idBrand
            ).collectLatest { result ->
                result.onSuccess {
                    if (it?.referenceNumber.isNullOrBlank()) {
                        uiState = uiState.copy(
                            showLoadingScreen = false,
                            showErrorScreen = true,
                            paymentSuccess = false
                        )
                    } else {
                        uiState = uiState.copy(
                            showLoadingScreen = false,
                            showErrorScreen = false,
                            paymentSuccess = true,
                            currentDate = getCurrentDate(Calendar.getInstance().time),
                            currentTime = getCurrentTime(Calendar.getInstance().time),
                            referenceNumber = it?.referenceNumber ?: ""
                        )
                    }
                }
                result.onFailure {
                    uiState = uiState.copy(
                        showLoadingScreen = false,
                        showErrorScreen = true,
                        paymentSuccess = false
                    )
                }
                result.onLoading {
                    uiState = uiState.copy(
                        showLoadingScreen = true,
                        showErrorScreen = false,
                        paymentSuccess = false
                    )
                }
            }
        }
    }

    private fun onCallProcessSinpeTransfer() {
        executeUseCase {
            processSinpeTransferUseCase.invoke(
                pkUser = editAmountHelper.pkUser.toIntOrNull() ?: 0,
                identification = editAmountHelper.identification,
                originCustomerIdentification = editAmountHelper.ibanAccount?.clientIdentification
                    ?: "",
                ibanAccountOrigin = editAmountHelper.ibanAccount?.sinpeAccount ?: "",
                originCustomerName = editAmountHelper.userName,
                idCurrencyOrigin = editAmountHelper.ibanCurrency?.id.toString(),
                ibanAccountDestination = editAmountHelper.smartAccount?.ibanAccountNumber ?: "",
                destinationCustomerIdentification = editAmountHelper.identification,
                destinationCustomerName = editAmountHelper.userName,
                idCurrencyDestination = editAmountHelper.smartCurrency?.id.toString(),
                reasonOfTransfer = DEFAULT_DESCRIPTION,
                transferType = SmartSinpeTransferType.REQUEST,
                amountToTransfer = if (editAmountHelper.shouldDisplayExchange) {
                    // Using this value cause endpoint expects amount in the same currency of the account
                    uiState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
                } else {
                    uiState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
                },
                exchangeRate = uiState.exchangeRate,
                idBrand = editAmountHelper.idBrand,
                user = editAmountHelper.userName
            ).collectLatest { result ->
                result.onSuccess {
                    if (it?.referenceNumber.isNullOrBlank()) {
                        uiState = uiState.copy(
                            showLoadingScreen = false,
                            showErrorScreen = true,
                            paymentSuccess = false
                        )
                    } else {
                        uiState = uiState.copy(
                            showLoadingScreen = false,
                            showErrorScreen = false,
                            paymentSuccess = true,
                            currentDate = getCurrentDate(Calendar.getInstance().time),
                            currentTime = getCurrentTime(Calendar.getInstance().time),
                            referenceNumber = it?.referenceNumber ?: ""
                        )
                    }
                }
                result.onFailure {
                    uiState = uiState.copy(
                        showLoadingScreen = false,
                        showErrorScreen = true,
                        paymentSuccess = false
                    )
                }
                result.onLoading {
                    uiState = uiState.copy(
                        showLoadingScreen = true,
                        showErrorScreen = false,
                        paymentSuccess = false
                    )
                }
            }
        }
    }

    private fun onAmountChanged(newAmount: String) {
        if (validateDecimalIncome(newAmount)) {
            val suggestions =
                listOf(uiState.minSuggestion, uiState.mediumSuggestion, uiState.maxSuggestion)
            val possibleSuggestion =
                suggestions.find { suggestion -> suggestion.value == newAmount }
            uiState = if (possibleSuggestion != null) {
                uiState.copy(
                    suggestedAmountSelected = possibleSuggestion,
                    currentAmountValueString = newAmount,
                    enableButton = newAmount.toDouble() > 0
                )
            } else {
                uiState.copy(
                    suggestedAmountSelected = null,
                    currentAmountValueString = newAmount,
                    enableButton = newAmount.isNotEmpty() && newAmount.toDouble() > 0
                )
            }
        }
    }

    private fun selectSuggestion(amount: SuggestedAmount) {
        uiState = uiState.copy(
            enableButton = amount.value.isNotEmpty() && amount.value.toDouble() > 0,
            currentAmountValueString = amount.value,
            suggestedAmountSelected = amount
        )
        getExchangeOnCompleted()
    }

    fun verifySuggestionSelected(order: SuggestionOrder) =
        uiState.suggestedAmountSelected?.isSelected(order) == true

    private fun onContinueClick() {
        uiState = uiState.copy(
            bottomSheetState = ModalBottomSheetState(Expanded)
        )
    }

    private fun onRetryTransfer() {
        uiState = uiState.copy(
            showErrorScreen = false,
            showLoadingScreen = true,
            paymentSuccess = false
        )
        if (editAmountHelper.idBrand == Brand.CostaRica.id) {
            onCallProcessSinpeTransfer()
        } else {
            onCallProcessTransferVisaToSmart()
        }
    }

    private fun onFailureWithDialog(isLoading: Boolean, dialogParameters: DialogParameters) {
        uiState =
            uiState.copy(
                isLoading = isLoading,
                openDialog = dialogParameters
            )
    }

    fun getFormattedAmount() =
        uiState.currency + uiState.currentAmountValueString?.stringToDoubleFormat(
            CURRENCY_SEPARATOR.toString()
        )

    fun getConvertedAmountFormatted() =
        "${editAmountHelper.ibanCurrency?.symbol}${
        uiState.exchangeConvertedAmount.formattedTwoDecimalsNumber().toString()
            .stringToDoubleFormat(CURRENCY_SEPARATOR.toString())
        }"

    private fun onNavigateToHome() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true
        )
    }

    private fun onNavigateBack() {
        val screen = when (editAmountHelper.previousScreen) {
            Screen.SmartPaymentAccountScreenCR.baseRoute -> Screen.SmartPaymentAccountScreenCR.route
            Screen.SmartPaymentCardsScreenSV.baseRoute -> Screen.SmartPaymentCardsScreenSV.route
            else -> Screen.HomeScreen.route
        }
        navigateBack(popTo = screen, isRestart = false)
    }

    data class UIState(
        // Interactions
        val suggestedAmountSelected: SuggestedAmount? = null,
        val minSuggestion: SuggestedAmount = SuggestedAmount(),
        val mediumSuggestion: SuggestedAmount = SuggestedAmount(),
        val maxSuggestion: SuggestedAmount = SuggestedAmount(),
        val currency: String = "",
        val currentAmountValueString: String? = null,
        val enableButton: Boolean = false,
        val isLoading: Boolean = false,
        val exchangeRate: Double = 0.0,
        val exchangeConvertedAmount: Double = 0.0,
        val exchangeRateLabel: String = "0.0",
        val convertedAmountLabel: String = "0.0",
        val placeholder: Int = R.string.smart_dollar_placeholder,
        val openDialog: DialogParameters = DialogParameters(),
        val bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(Hidden),
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
            is OnAmountCompleted -> getExchangeOnCompleted()
            is OnContinueClick -> onContinueClick()
            is OnSuggestedAmountClick -> selectSuggestion(uiEvent.suggestion)
            is OnCallProcessTransfer -> onProcessTransfer()
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
        data class OnSuggestedAmountClick(val suggestion: SuggestedAmount) : UIEvent()
        object OnContinueClick : UIEvent()
        object OnCallProcessTransfer : UIEvent()
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
