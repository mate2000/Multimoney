package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.ID_VISA_CARD
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SuggestedAmount
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class SavingAmountViewModelCopy @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val querySmartExchangeRateUseCase: QuerySmartExchangeRateUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    var idBrand: Int = 0
    var idCard: Int = 0
    var identification: String = ""
    var user: String = ""
    var smartCurrency: CurrencyType? = CurrencyType.Dollar
    var ibanCurrency: CurrencyType? = CurrencyType.Colon
    var shouldDisplayExchange: Boolean = true

    private fun onStart() {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idCard = savedStateHandle[ID_VISA_CARD] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        user = savedStateHandle[USER] ?: ""
        if (idBrand == Brand.CostaRica.id) {
            // Todo get accounts currency
            smartCurrency = CurrencyType.Dollar
            ibanCurrency = CurrencyType.Dollar
            shouldDisplayExchange = smartCurrency != ibanCurrency
            getSmartExchangeRate(
                user = user,
                identification = identification,
                idOriginCurrency = ibanCurrency?.currency ?: "",
                idDestinationCurrency = smartCurrency?.currency ?: ""
            )
        }
        uiState = uiState.copy(
            minSuggestion = SuggestedAmount.createSuggestion(
                smartCurrency == CurrencyType.Dollar,
                SuggestionOrder.MIN
            ),
            mediumSuggestion = SuggestedAmount.createSuggestion(
                smartCurrency == CurrencyType.Dollar,
                SuggestionOrder.MEDIUM
            ),
            maxSuggestion = SuggestedAmount.createSuggestion(
                smartCurrency == CurrencyType.Dollar,
                SuggestionOrder.MAX
            )
        )
    }

    private fun getSmartExchangeRate(
        user: String,
        identification: String,
        idOriginCurrency: String,
        idDestinationCurrency: String
    ) = executeUseCase {
        querySmartExchangeRateUseCase.invoke(
            user = user,
            identification = identification,
            idOriginCurrency = idOriginCurrency,
            idDestinationCurrency = idDestinationCurrency
        ).collectLatest { result ->
            result.onSuccess { rate ->
                uiState = uiState.copy(
                    isLoading = false,
                    exchangeRate = rate ?: 0.0
                )
            }
            result.onFailure {
                onUIEvent(
                    UIEvent.OnFailureWithDialog(
                        isLoading = false,
                        dialogParameters = DialogParameters(isActive = mutableStateOf(true))
                    )
                )
            }
            result.onLoading { uiState = uiState.copy(isLoading = true) }
        }
    }

    private fun onAmountChanged(newAmount: String) {
        val suggestions =
            listOf(uiState.minSuggestion, uiState.mediumSuggestion, uiState.maxSuggestion)
        val possibleSuggestion = suggestions.find { suggestion -> suggestion.value == newAmount }
        uiState = if (possibleSuggestion != null) {
            uiState.copy(
                currentAmountValueString = newAmount,
                suggestedAmountSelected = possibleSuggestion,
                enableButton = newAmount.toDouble() > 0
            )
        } else {
            uiState.copy(
                currentAmountValueString = newAmount,
                suggestedAmountSelected = null,
                enableButton = newAmount.isNotEmpty() && newAmount.toDouble() > 0
            )
        }
        if (shouldDisplayExchange) {
            // Todo Convert Amount
        }
    }

    private fun selectSuggestion(amount: SuggestedAmount) {
        uiState = uiState.copy(
            enableButton = amount.value.isNotEmpty() && amount.value.toDouble() > 0,
            currentAmountValueString = amount.value,
            suggestedAmountSelected = amount
        )
    }

    fun verifySuggestionSelected(order: SuggestionOrder) =
        uiState.suggestedAmountSelected?.isSelected(order) == true

    private fun onShowSavingBottomSheet() {
        uiState =
            uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded))
    }

    private fun onHideSavingBottomSheet() {
        uiState =
            uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Hidden))
    }

    private fun onContinueClick() {
        onUIEvent(UIEvent.OnShowPaymentBottomSheet)
    }

    private fun onFailureWithDialog(isLoading: Boolean, dialogParameters: DialogParameters) {
        uiState =
            uiState.copy(
                isLoading = isLoading,
                openDialog = dialogParameters
            )
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.PaymentSmartCardsScreen.route, isRestart = true)

    data class UIState(
        // Interactions
        val suggestedAmountSelected: SuggestedAmount? = null,
        val minSuggestion: SuggestedAmount = SuggestedAmount(),
        val mediumSuggestion: SuggestedAmount = SuggestedAmount(),
        val maxSuggestion: SuggestedAmount = SuggestedAmount(),
        val currentAmountValueString: String? = null,
        val enableButton: Boolean = false,
        val isLoading: Boolean = false,
        val exchangeRate: Double = 0.0,
        val exchangeConvertedAmount: Double = 0.0,
        val openDialog: DialogParameters = DialogParameters(),
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden
        )
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnStart -> onStart()
            is UIEvent.OnAmountValueChange -> onAmountChanged(uiEvent.value)
            is UIEvent.OnContinueClick -> onContinueClick()
            is UIEvent.OnHidePaymentBottomSheet -> onHideSavingBottomSheet()
            is UIEvent.OnShowPaymentBottomSheet -> onShowSavingBottomSheet()
            is UIEvent.OnSuggestedAmountClick -> selectSuggestion(uiEvent.suggestion)
            is UIEvent.OnFailureWithDialog -> onFailureWithDialog(
                uiEvent.isLoading,
                uiEvent.dialogParameters
            )
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnStart : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
        data class OnSuggestedAmountClick(val suggestion: SuggestedAmount) : UIEvent()
        object OnContinueClick : UIEvent()
        object OnShowPaymentBottomSheet : UIEvent()
        object OnHidePaymentBottomSheet : UIEvent()
        data class OnFailureWithDialog(
            val isLoading: Boolean,
            val dialogParameters: DialogParameters
        ) : UIEvent()
    }

    companion object {
        const val SAVING_PLACEHOLDER = "$0"
    }
}