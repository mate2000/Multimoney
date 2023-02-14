package com.multimoney.multimoney.presentation.ui.smart.common.editamount

import android.content.Context
import android.view.View
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.MutationProcessSinpeTransferUseCase
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.DESTINY_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ORIGIN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TRANSFER_TYPE
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.DisplayAccount
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban
import com.multimoney.multimoney.presentation.util.getMaskedVisaAccount
import com.multimoney.multimoney.presentation.util.stringToDoubleFormat
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import com.multimoney.multimoney.presentation.util.workers.startTimedNotification
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterialApi::class)
abstract class BaseSmartEditAmountViewModel : BaseViewModel(true) {

    @Inject
    protected lateinit var querySmartExchangeRateUseCase: QuerySmartExchangeRateUseCase

    @Inject
    protected lateinit var shareHelper: ShareHelper

    @Inject
    protected lateinit var savedStateHandle: SavedStateHandle

    @Inject
    protected lateinit var processSinpeTransferUseCase: MutationProcessSinpeTransferUseCase

    var amountUIState by mutableStateOf(AmountUIState())
        protected set

    // stateless
    var identification: String = ""
    var pkUser: String = ""
    var userName: String = ""
    var idBrand: Int = 0

    var transferType: Int = 0
    var smartAccount: SmartAccountID? = null
    var ibanAccount: IbanAccountID? = null
    var visaAccount: CardVisaDirect? = null
    var smartDestiny: SmartAccountID? = null

    /*
    Origin refers to the account where the money's going to be taken from
    Destination is the account to receive the money
    */
    var shouldDisplayExchange: Boolean = false
    var originCurrency: CurrencyType? = null
    var destinyCurrency: CurrencyType? = null
    var previousScreen: String = ""

    abstract fun onStart()

    suspend fun initializeValues() {
        idBrand = preferences.getIdBrand().first().toInt()
        identification = preferences.getIdentification().first()
        pkUser = preferences.getPkUser().first()
        userName = preferences.getUserName().first()
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        transferType = savedStateHandle[TRANSFER_TYPE] ?: 0

        when (transferType) {
            SmartTransferTypes.SmartToIban.id -> {
                smartAccount = savedStateHandle[ORIGIN_ACCOUNT]
                ibanAccount = savedStateHandle[DESTINY_ACCOUNT]
                originCurrency = smartAccount?.currencyID?.getCurrencyFromId()
                destinyCurrency = ibanAccount?.currencyId?.getCurrencyFromId()
                shouldDisplayExchange = originCurrency != destinyCurrency

                amountUIState = amountUIState.copy(
                    originAccountDisplay = DisplayAccount(
                        sheetTitleResource = originCurrency?.myAccountSmartSymbol,
                        sheetSubtitleResource = originCurrency?.currencyName,
                        icon = R.drawable.ic_multimoney_smart
                    ),
                    destinyAccountDisplay = DisplayAccount(
                        sheetTitle = ibanAccount?.nameAccount.orEmpty(),
                        sheetSubtitle = ibanAccount?.bank.orEmpty(),
                        sheetSubtitle2 = getMaskedAccountIban(ibanAccount?.sinpeAccount.orEmpty()),
                        icon = destinyCurrency?.accountIcon
                    ),
                    currency = destinyCurrency?.symbol ?: Dollar.symbol,
                    placeholder = if (destinyCurrency == Dollar) {
                        R.string.smart_dollar_placeholder
                    } else {
                        R.string.smart_colon_placeholder
                    }
                )
            }
            SmartTransferTypes.SmartToSmart.id -> {
                smartAccount = savedStateHandle[ORIGIN_ACCOUNT]
                smartDestiny = savedStateHandle[DESTINY_ACCOUNT]
                originCurrency = smartAccount?.currencyID?.getCurrencyFromId()
                destinyCurrency = smartDestiny?.currencyID?.getCurrencyFromId()
                shouldDisplayExchange = true

                amountUIState = amountUIState.copy(
                    originAccountDisplay = DisplayAccount(
                        sheetTitleResource = originCurrency?.myAccountSmartSymbol,
                        sheetSubtitleResource = originCurrency?.currencyName,
                        icon = R.drawable.ic_multimoney_smart
                    ),
                    destinyAccountDisplay = DisplayAccount(
                        sheetTitleResource = destinyCurrency?.myAccountSmartSymbol,
                        sheetSubtitleResource = destinyCurrency?.currencyName,
                        icon = R.drawable.ic_multimoney_smart
                    ),
                    currency = destinyCurrency?.symbol ?: Dollar.symbol,
                    placeholder = if (destinyCurrency == Dollar) {
                        R.string.smart_dollar_placeholder
                    } else {
                        R.string.smart_colon_placeholder
                    }
                )
            }
            SmartTransferTypes.IbanToSmart.id -> {
                smartAccount = savedStateHandle[DESTINY_ACCOUNT]
                ibanAccount = savedStateHandle[ORIGIN_ACCOUNT]
                originCurrency = ibanAccount?.currencyId?.getCurrencyFromId()
                destinyCurrency = smartAccount?.currencyID?.getCurrencyFromId()
                shouldDisplayExchange = originCurrency != destinyCurrency

                amountUIState = amountUIState.copy(
                    originAccountDisplay = DisplayAccount(
                        sheetTitle = ibanAccount?.bank.orEmpty(),
                        sheetSubtitle = getMaskedAccountIban(ibanAccount?.sinpeAccount.orEmpty()),
                        icon = originCurrency?.accountIcon
                    ),
                    destinyAccountDisplay = DisplayAccount(
                        sheetTitleResource = destinyCurrency?.myAccountSmartSymbol,
                        sheetSubtitleResource = destinyCurrency?.currencyName,
                        icon = R.drawable.ic_multimoney_smart
                    ),
                    currency = originCurrency?.symbol ?: Dollar.symbol,
                    placeholder = if (originCurrency == Dollar) {
                        R.string.smart_dollar_placeholder
                    } else {
                        R.string.smart_colon_placeholder
                    }
                )
            }
            SmartTransferTypes.VisaToSmart.id -> {
                visaAccount = savedStateHandle[ORIGIN_ACCOUNT]
                smartAccount = savedStateHandle[DESTINY_ACCOUNT]
                originCurrency = Dollar
                destinyCurrency = smartAccount?.currencyID?.getCurrencyFromId()
                shouldDisplayExchange = originCurrency != destinyCurrency

                amountUIState = amountUIState.copy(
                    originAccountDisplay = DisplayAccount(
                        sheetTitle = visaAccount?.detail.orEmpty(),
                        sheetSubtitle = getMaskedVisaAccount(visaAccount?.cardMaskedNumber.orEmpty()),
                        icon = R.drawable.ic_visa_card_item
                    ),
                    destinyAccountDisplay = DisplayAccount(
                        sheetTitleResource = destinyCurrency?.myAccountSmartSymbol,
                        icon = R.drawable.ic_multimoney_smart
                    ),
                    currency = originCurrency?.symbol ?: Dollar.symbol,
                    placeholder = if (originCurrency == Dollar) {
                        R.string.smart_dollar_placeholder
                    } else {
                        R.string.smart_colon_placeholder
                    }
                )
            }
        }
    }

    open fun getExchangeOnCompleted(
        isStart: Boolean = false,
        isPayment: Boolean = false,
        abbreviation: String? = destinyCurrency?.disbursementValue,
        idOriginCurrency: String = destinyCurrency?.id.toString(),
        idDestinationCurrency: String = originCurrency?.id.toString(),
        currentAmount: Double = amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
    ) {
        val amount = amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
        if (shouldDisplayExchange) {
            if (amount > 0.0 || isStart) {
                executeUseCase {
                    querySmartExchangeRateUseCase.invoke(
                        user = userName,
                        idBrand = idBrand,
                        abbreviation = abbreviation ?: "",
                        identification = identification,
                        idOriginCurrency = idOriginCurrency,
                        idDestinationCurrency = idDestinationCurrency,
                        amount = currentAmount
                    ).collectLatest { result ->
                        result.onFailure {
                            onFailureWithDialog(
                                false,
                                DialogParameters(isActive = mutableStateOf(true))
                            )
                        }
                        result.onLoading {
                            amountUIState = amountUIState.copy(isLoading = true)
                        }
                        result.onSuccess { rate ->
                            amountUIState = amountUIState.copy(
                                isLoading = false,
                                exchangeRate = rate?.exchangeRate ?: 0.0,
                                exchangeConvertedAmount = rate?.convertedAmount ?: 0.0,
                                exchangeRateLabel = rate?.exchangeRateLabel
                                    ?: "${idOriginCurrency.getCurrencySymbol()}0.0",
                                convertedAmountLabel = rate?.convertedAmountLabel
                                    ?: "${idDestinationCurrency.getCurrencySymbol()}0.0"
                            )
                            if (isPayment.not()) validateAmount()
                        }
                    }
                }
            }
        }
    }

    open fun onAmountCompleted() {
        /* Leaving this to call the other function so we can override this function in child classes according
        of how we need the exchange rate */
        getExchangeOnCompleted()
    }

    open fun validateAmount() {
        val currentAmount = if (shouldDisplayExchange) {
            amountUIState.exchangeConvertedAmount
        } else {
            amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
        }

        val isAmountValid = currentAmount <= (smartAccount?.totalBalance ?: 0.0)
        amountUIState = amountUIState.copy(
            isAmountValid = isAmountValid,
            bottomSheetState = ModalBottomSheetState(Hidden),
            enableButton = validateForm(isAmountValid = isAmountValid)
        )
    }

    open fun onAmountChanged(newAmount: String) {
        if (validateDecimalIncome(newAmount)) {
            amountUIState = amountUIState.copy(
                currentAmountValueString = newAmount
            )
        }
    }

    open fun validateForm(
        newAmount: String? = amountUIState.currentAmountValueString,
        newMotive: String = amountUIState.motive,
        isAmountValid: Boolean = amountUIState.isAmountValid
    ) = (newAmount?.isNotEmpty() == true) &&
        (newAmount.toDoubleOrNull() ?: 0.0) > 0.0 &&
        newMotive.isNotEmpty() &&
        isAmountValid

    abstract fun onContinueClick()

    open fun onMotiveChange(newMotive: String) {
        amountUIState = amountUIState.copy(
            motive = newMotive,
            enableButton = validateForm(newMotive = newMotive)
        )
    }

    open fun onCallProcessSinpeTransfer(
        originIdentification: String,
        originAccountNumber: String,
        originCustomerName: String,
        originCurrency: String,
        destinationIdentification: String,
        destinationAccountNumber: String,
        destinationCurrency: String,
        destinationCustomerName: String,
        transferType: SmartSinpeTransferType
    ) {
        executeUseCase {
            processSinpeTransferUseCase.invoke(
                pkUser = pkUser.toIntOrNull() ?: 0,
                identification = identification,
                ibanAccountOrigin = originAccountNumber,
                originCustomerIdentification = originIdentification,
                originCustomerName = originCustomerName,
                idCurrencyOrigin = originCurrency,
                destinationCustomerIdentification = destinationIdentification,
                ibanAccountDestination = destinationAccountNumber,
                destinationCustomerName = destinationCustomerName,
                idCurrencyDestination = destinationCurrency,
                reasonOfTransfer = amountUIState.motive.ifEmpty { DEFAULT_DESCRIPTION },
                transferType = transferType,
                amountToTransfer = amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0,
                exchangeRate = if (amountUIState.exchangeRate == 0.0) 1.0 else amountUIState.exchangeRate,
                idBrand = idBrand,
                user = userName
            ).collectLatest { result ->
                result.onSuccess {
                    if (it?.referenceNumber.isNullOrBlank()) {
                        amountUIState = amountUIState.copy(
                            showLoadingScreen = false,
                            showErrorScreen = true,
                            paymentSuccess = false
                        )
                    } else {
                        amountUIState = amountUIState.copy(
                            showLoadingScreen = false,
                            showErrorScreen = false,
                            paymentSuccess = true,
                            currentDate = getCurrentDate(Calendar.getInstance().time),
                            currentTime = getCurrentTime(Calendar.getInstance().time).lowercase(),
                            referenceNumber = it?.referenceNumber ?: ""
                        )
                    }
                }
                result.onFailure {
                    amountUIState = amountUIState.copy(
                        showLoadingScreen = false,
                        showErrorScreen = true,
                        paymentSuccess = false
                    )
                }
                result.onLoading {
                    amountUIState = amountUIState.copy(
                        showLoadingScreen = true,
                        showErrorScreen = false,
                        paymentSuccess = false
                    )
                }
                result.onMessage {
                    amountUIState = amountUIState.copy(
                        errorMessage = it?.messageError?.message ?: "",
                        errorDetail = it?.messageError?.detail ?: "",
                        showLoadingScreen = false,
                        showErrorScreen = true,
                        paymentSuccess = false
                    )
                }
            }
        }
    }

    abstract fun onProcessTransfer()

    protected open fun onTryLater(
        notificationTitle: String,
        notificationBody: String,
        notificationSmallIcon: Int,
        context: Context,
        navigateBack: () -> Unit
    ) {
        startTimedNotification(
            context,
            notificationTitle,
            notificationBody,
            notificationSmallIcon
        )
        navigateBack()
    }

    protected open fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        shareHelper.sharedScreenShot(view, capturingBounds)
    }

    protected open fun onFailureWithDialog(isLoading: Boolean, dialogParameters: DialogParameters) {
        amountUIState =
            amountUIState.copy(
                isLoading = isLoading,
                openDialog = dialogParameters
            )
    }

    protected open fun onAbandonFlow() {
        amountUIState = amountUIState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.smart_iban_transfer_abandon_dialog_title,
                descriptionResource = R.string.smart_iban_transfer_abandon_dialog_message,
                isActive = mutableStateOf(true),
                positiveResource = R.string.cancel,
                negativeResource = R.string.button_continue,
                negativeAction = { onNavigateToHome() }
            )
        )
    }

    protected open fun onRetryTransfer() {
        amountUIState = amountUIState.copy(
            showErrorScreen = false,
            showLoadingScreen = false,
            paymentSuccess = false
        )
        onContinueClick()
    }

    protected open fun onNavigateToHome() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.COLLAPSED
        )
    }

    abstract fun onNavigateBack()

    fun getFormattedAmount() =
        amountUIState.currency + amountUIState.currentAmountValueString?.stringToDoubleFormat()

    data class AmountUIState(
        // Interactions
        val currency: String = "",
        val currentAmountValueString: String? = null,
        val motive: String = "",
        val enableButton: Boolean = false,
        val isAmountValid: Boolean = true,
        val isLoading: Boolean = false,
        val exchangeRate: Double = 0.0,
        val exchangeConvertedAmount: Double = 0.0,
        val exchangeRateLabel: String? = null,
        val convertedAmountLabel: String? = null,
        val placeholder: Int = R.string.smart_dollar_placeholder,
        val openDialog: DialogParameters = DialogParameters(),
        val idCard: Long = 0,
        val bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
        val cardBankName: String = "",
        val errorMessage: String = "",
        val errorDetail: String = "",
        val showErrorScreen: Boolean = false,
        val showLoadingScreen: Boolean = false,
        val paymentSuccess: Boolean = false,
        val referenceNumber: String = "",
        val currentDate: String = "",
        val currentTime: String = "",
        val originAccountDisplay: DisplayAccount? = null,
        val destinyAccountDisplay: DisplayAccount? = null
    )

    open fun onAmountUIEvent(uiEvent: AmountUIEvent) {
        when (uiEvent) {
            is AmountUIEvent.OnNavigateBack -> onNavigateBack()
            is AmountUIEvent.OnStart -> onStart()
            is AmountUIEvent.OnAmountValueChange -> onAmountChanged(uiEvent.value)
            is AmountUIEvent.OnAmountCompleted -> onAmountCompleted()
            is AmountUIEvent.OnMotiveChange -> onMotiveChange(uiEvent.value)
            is AmountUIEvent.OnContinueClick -> onContinueClick()
            is AmountUIEvent.OnAbandonFlow -> onAbandonFlow()
            is AmountUIEvent.OnCallProcessTransfer -> onProcessTransfer()
            is AmountUIEvent.OnFailureWithDialog -> onFailureWithDialog(
                uiEvent.isLoading,
                uiEvent.dialogParameters
            )
            is AmountUIEvent.OnRetryTransfer -> onRetryTransfer()
            is AmountUIEvent.OnTryLater -> onTryLater(
                uiEvent.notificationTitle,
                uiEvent.notificationBody,
                uiEvent.notificationSmallIcon,
                uiEvent.context
            ) { onNavigateToHome() }
            is AmountUIEvent.OnNavigateHome -> onNavigateToHome()
            is AmountUIEvent.OnShareVoucherImage -> onShareVoucherImage(
                uiEvent.view,
                uiEvent.capturingBounds
            )
        }
    }

    sealed class AmountUIEvent {
        object OnNavigateBack : AmountUIEvent()
        object OnStart : AmountUIEvent()
        data class OnAmountValueChange(val value: String) : AmountUIEvent()
        data class OnMotiveChange(val value: String) : AmountUIEvent()
        data class OnAmountCompleted(val value: String) : AmountUIEvent()
        object OnContinueClick : AmountUIEvent()
        object OnAbandonFlow : AmountUIEvent()
        object OnCallProcessTransfer : AmountUIEvent()
        object OnRetryTransfer : AmountUIEvent()
        data class OnFailureWithDialog(
            val isLoading: Boolean,
            val dialogParameters: DialogParameters
        ) : AmountUIEvent()

        data class OnTryLater(
            val notificationTitle: String,
            val notificationBody: String,
            val notificationSmallIcon: Int,
            val context: Context
        ) : AmountUIEvent()

        object OnNavigateHome : AmountUIEvent()
        data class OnShareVoucherImage(
            val view: View,
            val capturingBounds: Rect
        ) : AmountUIEvent()
    }

    companion object {
        const val DEFAULT_DESCRIPTION = "Depósito cuenta Smart"
        const val CURRENCY_SEPARATOR = ','
    }
}
