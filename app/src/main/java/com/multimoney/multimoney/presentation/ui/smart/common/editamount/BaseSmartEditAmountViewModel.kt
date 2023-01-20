package com.multimoney.multimoney.presentation.ui.smart.common.editamount

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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationProcessSinpeTransferUseCase
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
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
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getCurrencySymbol
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import com.multimoney.multimoney.presentation.util.stringToDoubleFormat
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import com.multimoney.multimoney.presentation.util.workers.startTimedNotification
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

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
    var idCard: Long = 0
    var identification: String = ""
    var pkUser: String = ""
    var userName: String = ""
    var idCurrency: Int = 0
    var tokenNumber: Long = 0

    /* For Smart to Iban
    Origin refers to the account where the money's going to be taken from
    Destination is the account to receive the money
    */
    var smartAccount: SmartAccountID? = null
    var ibanAccount: IbanAccountID? = null

    // For Smart to Smart
    var destinationSmartAccount: SmartAccountID? = null

    var smartCurrency: CurrencyType? = CurrencyType.Dollar
    var smartDestinationCurrency: CurrencyType? = CurrencyType.Dollar
    var ibanCurrency: CurrencyType? = null
    var idBrand: Int = 0
    var shouldDisplayExchange: Boolean = false
    var maskedCardNumber: String = ""
    var bankDetail: String = ""
    var previousScreen: String = ""
    var sheetSubtitle: Int = R.string.smart_payment_amount_bottom_sheet_from_card
    var originTitle: Int = R.string.empty
    var originIcon: Int = R.drawable.ic_visa_card_item
    var accountName: String = ""

    abstract fun onStart()

    suspend fun initializeValues() {
        idBrand = preferences.getIdBrand().first().toInt()
        identification = preferences.getIdentification().first()
        pkUser = preferences.getPkUser().first()
        userName = preferences.getUserName().first()
        smartAccount = savedStateHandle[SMART_IDS]
        smartCurrency = smartAccount?.currencyID?.getCurrencyFromId()
        tokenNumber = smartAccount?.tokenAccount?.toLongOrNull() ?: 0
        idCurrency = smartAccount?.currencyID ?: 0
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""

        if (idBrand == Brand.CostaRica.id) {
            initializeCRValues()
        } else {
            initializeSVValues()
        }
    }

    open fun initializeCRValues() {
        ibanAccount = savedStateHandle[IBAN_ACCOUNT]
        ibanCurrency = if (ibanAccount != null) {
            ibanAccount?.currencyId?.getCurrencyFromId()
        } else if (destinationSmartAccount != null) {
            destinationSmartAccount?.currencyID?.getCurrencyFromId()
        } else {
            null
        }
        shouldDisplayExchange = smartCurrency != ibanCurrency
        bankDetail = ibanAccount?.bank ?: ""
        maskedCardNumber = ibanAccount?.sinpeAccount ?: ""
        sheetSubtitle = R.string.smart_payment_amount_bottom_sheet_from_card_CR
        originTitle = R.string.smart_payment_origin_account_label
        originIcon =
            ibanCurrency?.id?.getCurrencyFromId()?.accountIcon
                ?: CurrencyType.Colon.accountIcon

        accountName = ibanAccount?.nameAccount ?: ""
        smartDestinationCurrency = when (smartCurrency?.value) {
            CurrencyType.Dollar.value -> {
                CurrencyType.Colon
            }
            CurrencyType.Colon.value -> {
                CurrencyType.Dollar
            }
            else -> {
                null
            }
        }
    }

    open fun initializeSVValues() {
        idCard = savedStateHandle[ID_VISA_CARD] ?: 0
        maskedCardNumber = savedStateHandle[MASKED_CARD] ?: ""
        bankDetail = savedStateHandle[BANK_DETAIL] ?: ""
        sheetSubtitle = R.string.smart_payment_amount_bottom_sheet_from_card
        originTitle = R.string.smart_payment_card_bank_label
        originIcon = R.drawable.ic_visa_card_item
    }

    open fun getExchangeOnCompleted(
        isStart: Boolean = false,
        abbreviation: String? = ibanCurrency?.disbursementValue
            ?: smartDestinationCurrency?.disbursementValue,
        idOriginCurrency: String = smartCurrency?.id.toString(),
        idDestinationCurrency: String = ibanCurrency?.id?.toString()
            ?: smartDestinationCurrency?.id.toString(),
        currentAmount: Double = amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
    ) {
        val amount = amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
        if (shouldDisplayExchange) {
            if ((amountUIState.isAmountValid && amount > 0.0) || isStart) {
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

    open fun onAmountChanged(newAmount: String) {
        if (validateDecimalIncome(newAmount)) {
            amountUIState = amountUIState.copy(
                currentAmountValueString = newAmount,
                enableButton = validateForm(newAmount = newAmount),
                isAmountValid = true
            )
        }
    }

    open fun validateForm(
        newAmount: String? = amountUIState.currentAmountValueString,
        newMotive: String = amountUIState.motive
    ) = (newAmount?.isNotEmpty() == true) && (
        newAmount.toDoubleOrNull()
            ?: 0.0
        ) > 0.0 && newMotive.isNotEmpty()

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
            isRestart = true
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
        const val ID_NOT_APPLICABLE = -1
        const val NOT_APPLICABLE = "NA"
        const val CURRENCY_SEPARATOR = ','
    }
}
