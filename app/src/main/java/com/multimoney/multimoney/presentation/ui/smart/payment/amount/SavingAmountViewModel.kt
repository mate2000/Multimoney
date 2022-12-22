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
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationProcessTransferVisaToSmartVDUseCase
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.IBAN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ID_VISA_CARD
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.BANK_DETAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.MASKED_CARD
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnCallProcessTransferVisaToSmart
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnShareVoucherImage
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnSuggestedAmountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnTryLater
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SuggestedAmount
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import com.multimoney.multimoney.presentation.util.workers.startTimedNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class, FlowPreview::class)
class SavingAmountViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val processTransferVisaToSmart: MutationProcessTransferVisaToSmartVDUseCase,
    private val querySmartExchangeRateUseCase: QuerySmartExchangeRateUseCase,
    private val shareHelper: ShareHelper,
    private val dataStorePreferences: DataStorePreferences,
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
    private var smartAccount: SmartAccountID? = null
    private var ibanAccount: SinpeAccount? = null
    var smartCurrency: CurrencyType? = Dollar
    var ibanCurrency: CurrencyType? = null
    var shouldDisplayExchange: Boolean = false
    var maskedCardNumber: String = ""
    var bankDetail: String = ""
    var previousScreen: String = ""

    private fun onStart() {
        viewModelScope.launch {
            idBrand = dataStorePreferences.getIdBrand().first().toInt()
            identification = dataStorePreferences.getIdentification().first()
            user = dataStorePreferences.getPkUser().first()
            idCard = savedStateHandle[ID_VISA_CARD] ?: 0
            smartAccount = savedStateHandle[SMART_IDS]
            smartCurrency = smartAccount?.currencyID?.getCurrencyFromId()
            idCurrency = smartAccount?.currencyID ?: 0
            tokenNumber = smartAccount?.tokenAccount?.toLongOrNull() ?: 0
            maskedCardNumber = savedStateHandle[MASKED_CARD] ?: ""
            bankDetail = savedStateHandle[BANK_DETAIL] ?: ""

            if (idBrand == Brand.CostaRica.id) {
                ibanAccount = savedStateHandle[IBAN_ACCOUNT]
                ibanCurrency = ibanAccount?.currencyId?.getCurrencyFromId()
                shouldDisplayExchange = smartCurrency != ibanCurrency
                getSmartExchangeRate(
                    user = user,
                    identification = identification,
                    idOriginCurrency = ibanCurrency?.currency ?: "",
                    idDestinationCurrency = smartCurrency?.currency ?: ""
                )
            }

            uiState = uiState.copy(
                currency = smartCurrency?.symbol ?: Dollar.symbol,
                placeholder = if (smartCurrency == Dollar) R.string.smart_dollar_placeholder else R.string.smart_colon_placeholder,
                minSuggestion = SuggestedAmount.createSuggestion(
                    smartCurrency == Dollar,
                    SuggestionOrder.MIN
                ),
                mediumSuggestion = SuggestedAmount.createSuggestion(
                    smartCurrency == Dollar,
                    SuggestionOrder.MEDIUM
                ),
                maxSuggestion = SuggestedAmount.createSuggestion(
                    smartCurrency == Dollar,
                    SuggestionOrder.MAX
                )
            )
        }
    }

    private fun getSmartExchangeRate(
        user: String,
        identification: String,
        idOriginCurrency: String,
        idDestinationCurrency: String
    ) = executeUseCase {
        uiState.currentAmountValueString.debounce(TWO_SECONDS).collectLatest {
            querySmartExchangeRateUseCase.invoke(
                user = user,
                idBrand = idBrand,
                abbreviation = smartCurrency?.disbursementValue ?: "",
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

    private fun onCallProcessTransferVisaToSmart() {
        executeUseCase {
            processTransferVisaToSmart.invoke(
                idCard,
                tokenNumber,
                identification,
                uiState.currentAmountValueString.firstOrNull() ?: "",
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
            uiState.currentAmountValueString.value = newAmount
            uiState.copy(
                suggestedAmountSelected = possibleSuggestion,
                enableButton = newAmount.toDouble() > 0
            )
        } else {
            uiState.currentAmountValueString.value = newAmount
            uiState.copy(
                suggestedAmountSelected = null,
                enableButton = newAmount.isNotEmpty() && newAmount.toDouble() > 0
            )
        }
    }

    private fun selectSuggestion(amount: SuggestedAmount) {
        uiState.currentAmountValueString.value = amount.value
        uiState = uiState.copy(
            enableButton = amount.value.isNotEmpty() && amount.value.toDouble() > 0,
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

    private fun onNavigateBack() =
        navigateBack(popTo = previousScreen, isRestart = true)

    data class UIState(
        // Interactions
        val suggestedAmountSelected: SuggestedAmount? = null,
        val minSuggestion: SuggestedAmount = SuggestedAmount(),
        val mediumSuggestion: SuggestedAmount = SuggestedAmount(),
        val maxSuggestion: SuggestedAmount = SuggestedAmount(),
        val currency: String = "",
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
        object OnStart : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
        data class OnSuggestedAmountClick(val suggestion: SuggestedAmount) : UIEvent()
        object OnContinueClick : UIEvent()
        object OnCallProcessTransferVisaToSmart : UIEvent()
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

    companion object {
        const val SAVING_PLACEHOLDER_DOLLAR = "$0"
        const val SAVING_PLACEHOLDER_COLON = "₡000"
        const val DEFAULT_DESCRIPTION = "Smart account deposit"
        const val ID_NOT_APPLICABLE = -1
        const val NOT_APPLICABLE = "NA"
        const val TWO_SECONDS = 2000L
    }
}
