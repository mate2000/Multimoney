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
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.MutationProcessTransferVisaToSmartVDUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.ID_VISA_CARD
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ACCOUNT_TOKEN
import com.multimoney.multimoney.presentation.navigation.navgraph.BANK_DETAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.MASKED_CARD
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnCallProcessTransferVisaToSmart
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnSuggestedAmountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnTryLater
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Colon
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
import com.multimoney.multimoney.presentation.util.catalog.SuggestedAmount
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import com.multimoney.multimoney.presentation.util.workers.startTimedNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class SavingAmountViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val processTransferVisaToSmart: MutationProcessTransferVisaToSmartVDUseCase,
    private val shareHelper: ShareHelper
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    private var idBrand: Int = 0
    private var idCard: Long = 0
    private var identification: String = ""
    private var user: String = ""
    private var idCurrency: Int = 0
    private var tokenNumber: Long = 0
    var maskedCardNumber: String = ""
    var bankDetail: String = ""

    private fun onStart() {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idCard = savedStateHandle[ID_VISA_CARD] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        user = savedStateHandle[USER] ?: ""
        idCurrency = savedStateHandle[ID_CURRENCY] ?: 0
        tokenNumber = savedStateHandle[ACCOUNT_TOKEN] ?: 0
        maskedCardNumber = savedStateHandle[MASKED_CARD] ?: ""
        bankDetail = savedStateHandle[BANK_DETAIL] ?: ""

        // Todo get if the account is using dollars
        uiState = uiState.copy(
            currency = if (idCurrency == Dollar.id) Dollar.symbol else Colon.symbol,
            minSuggestion = SuggestedAmount.createSuggestion(
                idCurrency == Dollar.id,
                SuggestionOrder.MIN
            ),
            mediumSuggestion = SuggestedAmount.createSuggestion(
                idCurrency == Dollar.id,
                SuggestionOrder.MEDIUM
            ),
            maxSuggestion = SuggestedAmount.createSuggestion(
                idCurrency == Dollar.id,
                SuggestionOrder.MAX
            )
        )
    }

    private fun onCallProcessTransferVisaToSmart() {
        executeUseCase {
            processTransferVisaToSmart.invoke(
                idCard,
                tokenNumber,
                identification,
                uiState.currentAmountValueString ?: "",
                idCurrency,
                DEFAULT_DESCRIPTION,
                maskedCardNumber,
                user,
                idBrand
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
        onCallProcessTransferVisaToSmart()
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

    private fun onNavigateToHome() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true
        )
    }

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.SmartPaymentCardsScreenSV.route, isRestart = true)

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
        val idCard: Long = 0,
        val bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(Hidden),
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
            is OnContinueClick -> onContinueClick()
            is OnSuggestedAmountClick -> selectSuggestion(uiEvent.suggestion)
            is OnCallProcessTransferVisaToSmart -> onCallProcessTransferVisaToSmart()
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
        object OnStart : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
        data class OnSuggestedAmountClick(val suggestion: SuggestedAmount) : UIEvent()
        object OnContinueClick : UIEvent()
        object OnCallProcessTransferVisaToSmart : UIEvent()
        object OnRetryTransfer : UIEvent()
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

    companion object {
        const val SAVING_PLACEHOLDER = "$0"
        const val DEFAULT_DESCRIPTION = "Smart account deposit"
    }
}
