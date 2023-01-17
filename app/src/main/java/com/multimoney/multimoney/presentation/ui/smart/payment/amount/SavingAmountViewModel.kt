package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationProcessTransferVisaToSmartVDUseCase
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnSuggestedAmountClick
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
import com.multimoney.multimoney.presentation.util.catalog.SuggestedAmount
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class SavingAmountViewModel @Inject constructor(
    private val processTransferVisaToSmart: MutationProcessTransferVisaToSmartVDUseCase
) : BaseSmartEditAmountViewModel() {

    var uiState by mutableStateOf(UIState())
        private set

    override fun onStart() {
        viewModelScope.launch {
            initializeValues()
            uiState = uiState.copy(
                minSuggestion = SuggestedAmount.createSuggestion(
                    originCurrency == Dollar,
                    SuggestionOrder.MIN
                ),
                mediumSuggestion = SuggestedAmount.createSuggestion(
                    originCurrency == Dollar,
                    SuggestionOrder.MEDIUM
                ),
                maxSuggestion = SuggestedAmount.createSuggestion(
                    originCurrency == Dollar,
                    SuggestionOrder.MAX
                )
            )
            getExchangeOnCompleted(
                isStart = true,
                abbreviation = destinyCurrency?.disbursementValue ?: "",
                idOriginCurrency = originCurrency?.id.toString(),
                idDestinationCurrency = destinyCurrency?.id.toString()
            )
        }
    }

    override fun onProcessTransfer() {
        if (idBrand == Brand.CostaRica.id) {
            onCallProcessSinpeTransfer(
                originIdentification = ibanAccount?.clientIdentification ?: "",
                originAccountNumber = ibanAccount?.sinpeAccount ?: "",
                originCustomerName = ibanAccount?.nameAccount ?: "",
                originCurrency = originCurrency?.id.toString(),
                destinationIdentification = identification,
                destinationAccountNumber = smartAccount?.ibanAccountNumber ?: "",
                destinationCurrency = destinyCurrency?.id.toString(),
                destinationCustomerName = userName,
                transferType = SmartSinpeTransferType.REQUEST
            )
        } else if (idBrand == Brand.ElSalvador.id) {
            onCallProcessTransferVisaToSmart()
        }
    }

    private fun onCallProcessTransferVisaToSmart() {
        executeUseCase {
            processTransferVisaToSmart.invoke(
                visaAccount?.idCard?.toLong() ?: 0,
                smartAccount?.tokenAccount?.toLongOrNull() ?: 0,
                identification,
                baseUIState.currentAmountValueString ?: "",
                smartAccount?.currencyID ?: 0,
                DEFAULT_DESCRIPTION,
                visaAccount?.cardMaskedNumber ?: "",
                pkUser,
                idBrand
            ).collectLatest { result ->
                result.onSuccess {
                    if (it?.referenceNumber.isNullOrBlank()) {
                        baseUIState = baseUIState.copy(
                            showLoadingScreen = false,
                            showErrorScreen = true,
                            paymentSuccess = false
                        )
                    } else {
                        baseUIState = baseUIState.copy(
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
                    baseUIState = baseUIState.copy(
                        showLoadingScreen = false,
                        showErrorScreen = true,
                        paymentSuccess = false
                    )
                }
                result.onLoading {
                    baseUIState = baseUIState.copy(
                        showLoadingScreen = true,
                        showErrorScreen = false,
                        paymentSuccess = false
                    )
                }
            }
        }
    }

    override fun onAmountCompleted() {
        getExchangeOnCompleted(
            abbreviation = destinyCurrency?.disbursementValue ?: "",
            idOriginCurrency = originCurrency?.id.toString(),
            idDestinationCurrency = destinyCurrency?.id.toString()
        )
    }

    override fun onAmountChanged(newAmount: String) {
        if (validateDecimalIncome(newAmount)) {
            val suggestions =
                listOf(uiState.minSuggestion, uiState.mediumSuggestion, uiState.maxSuggestion)
            val possibleSuggestion =
                suggestions.find { suggestion -> suggestion.value == newAmount }
            if (possibleSuggestion != null) {
                uiState = uiState.copy(
                    suggestedAmountSelected = possibleSuggestion
                )
                baseUIState = baseUIState.copy(
                    currentAmountValueString = newAmount,
                    enableButton = newAmount.toDouble() > 0
                )
            } else {
                uiState = uiState.copy(
                    suggestedAmountSelected = null
                )
                baseUIState = baseUIState.copy(
                    currentAmountValueString = newAmount,
                    enableButton = newAmount.isNotEmpty() && newAmount.toDouble() > 0
                )
            }
        }
    }

    private fun selectSuggestion(amount: SuggestedAmount) {
        baseUIState = baseUIState.copy(
            enableButton = amount.value.isNotEmpty() && amount.value.toDouble() > 0,
            currentAmountValueString = amount.value
        )
        uiState = uiState.copy(
            suggestedAmountSelected = amount
        )
        onAmountCompleted()
    }

    fun verifySuggestionSelected(order: SuggestionOrder) =
        uiState.suggestedAmountSelected?.isSelected(order) == true

    override fun onContinueClick() {
        baseUIState = baseUIState.copy(
            bottomSheetState = ModalBottomSheetState(Expanded)
        )
    }

    override fun onNavigateBack() {
        val screen = when (previousScreen) {
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
        val maxSuggestion: SuggestedAmount = SuggestedAmount()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnSuggestedAmountClick -> selectSuggestion(uiEvent.suggestion)
        }
    }

    sealed class UIEvent {
        data class OnSuggestedAmountClick(val suggestion: SuggestedAmount) : UIEvent()
    }
}
