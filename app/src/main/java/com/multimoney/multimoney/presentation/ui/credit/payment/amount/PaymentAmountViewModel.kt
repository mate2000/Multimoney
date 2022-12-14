package com.multimoney.multimoney.presentation.ui.credit.payment.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.credit.MutationActivateClientAutomaticDebitUseCase
import com.multimoney.domain.interaction.credit.MutationProcessPaymentListUseCase
import com.multimoney.domain.interaction.credit.QueryGetExchangeRateCreditUseCase
import com.multimoney.domain.model.balance.Summary
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.credit.DestinyAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.NAME_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnAlertResultButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnAutomaticProgrammedPaymentCheckedChanged
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnCallQueryGetExchangeRateCredit
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnHidePaymentBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnMaximumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnMinimumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnNavigateToVoucher
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnProcessPayment
import com.multimoney.multimoney.presentation.util.formattedTwoDecimalsNumber
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.stringToDoubleFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class PaymentAmountViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mutationProcessPaymentUseCase: MutationProcessPaymentListUseCase,
    private val queryGetExchangeRateCreditUseCase: QueryGetExchangeRateCreditUseCase,
    private val mutationActivateClientAutomaticDebitUseCase: MutationActivateClientAutomaticDebitUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String? = null
    private var idBrand: Int? = null
    private var idClient: Int? = null
    private var idLoanClient: Int? = null
    var summaryList: List<Summary>? = null
    private var minimumPayment: Int = 0
    private var maximumPayment: Int = 0
    private var identification: String? = null
    private var userName: String? = null
    private var paymentDate: String? = null
    private var referenceNumber: String? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0
        summaryList = savedStateHandle.get<Array<Summary>>(SUMMARY_LIST)?.toList()
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        userName = savedStateHandle[NAME_CLIENT] ?: ""
        paymentDate = savedStateHandle[PAYMENT_DATE]

        onInitializeInteractionValues()
    }

    private fun onInitializeInteractionValues() {
        var minimumPaymentLabel = ""
        var maximumPaymentLabel = ""
        var isAmountVisible = false
        if (isMultiCurrency()) {
            summaryList?.forEachIndexed { index, summary ->
                if (index < (summaryList?.lastIndex ?: 0)) {
                    minimumPaymentLabel = minimumPaymentLabel.plus(summary.minPaymentLabel).plus(PAYMENT_PLUS)
                    maximumPaymentLabel = maximumPaymentLabel.plus(summary.currentBalanceLabel).plus(PAYMENT_PLUS)
                } else {
                    minimumPaymentLabel = minimumPaymentLabel.plus(summary.minPaymentLabel)
                    maximumPaymentLabel = maximumPaymentLabel.plus(summary.currentBalanceLabel)
                }
            }
        } else if (summaryList?.isNotEmpty() == true && summaryList?.firstOrNull() != null) {
            val summary = summaryList?.first()
            minimumPaymentLabel = summary?.minPaymentLabel.orEmpty()
            maximumPaymentLabel = summary?.currentBalanceLabel.orEmpty()
            minimumPayment = summary?.minPayment?.toInt() ?: 0
            maximumPayment = summary?.currentBalance?.roundToInt() ?: 0
            isAmountVisible = true
        }
        uiState = uiState.copy(
            minimumPaymentLabel = minimumPaymentLabel,
            maximumPaymentLabel = maximumPaymentLabel,
            isAmountVisible = isAmountVisible,
            currency = minimumPaymentLabel.first().toString(),
            clientBankAccount = savedStateHandle[CLIENT_BANK_ACCOUNT]
        )
        uiState = uiState.copy(
            accountCurrency = if (isMultiCurrency() || shouldDisplayExchangeRate()) {
                uiState.clientBankAccount?.idCurrency?.getCurrencyFromId()?.symbol ?: ""
            } else {
                minimumPaymentLabel.first().toString()
            }
        )
        onAmountValueChange(minimumPayment.toString())
    }

    fun shouldDisplayExchangeRate() =
        isMultiCurrency() ||
            uiState.clientBankAccount?.idCurrency?.toString() != summaryList?.first()?.idCurrency?.toString()

    fun isMultiCurrency() = (summaryList?.count() ?: 1) > 1

    private fun onShowPaymentBottomSheet() {
        uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded))
    }

    private fun onHidePaymentBottomSheet() {
        uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Hidden))
    }

    private fun onAmountButtonClick(isMinimumSelected: Boolean) {
        val value = if (isMinimumSelected) {
            minimumPayment.toString()
        } else {
            maximumPayment.toString()
        }
        uiState = uiState.copy(
            currentAmountValueString = value,
            isMinimumSelected = isMinimumSelected,
            isMaximumSelected = !isMinimumSelected,
            enableButton = (
                isMultiCurrency() || (
                    value.isNotEmpty() && value.toInt() <= maximumPayment &&
                        value.isNotEmpty() && value.toInt() > PAYMENT_MUST_HIGHER_THAN_VALUE
                    )
                ),
            currentAmountError = if (value.isNotEmpty() && value.toInt() > maximumPayment) {
                Pair(true, R.string.payment_amount_amount_max_error)
            } else if (value.isNotEmpty() && value.toInt() <= PAYMENT_MUST_HIGHER_THAN_VALUE) {
                Pair(true, R.string.payment_amount_amount_min_error)
            } else {
                Pair(false, R.string.empty)
            }
        )
    }

    private fun onPaymentButtonClick() {
        if (shouldDisplayExchangeRate()) {
            onUIEvent(OnCallQueryGetExchangeRateCredit)
        } else {
            onShowPaymentBottomSheet()
        }
    }

    private fun onAmountValueChange(value: String) {
        uiState = uiState.copy(
            currentAmountValueString = value,
            isMinimumSelected = value.isNotEmpty() && value.toInt() == minimumPayment,
            isMaximumSelected = value.isNotEmpty() && value.toInt() == maximumPayment,
            enableButton = (
                value.isNotEmpty() && value.toInt() <= maximumPayment &&
                    value.isNotEmpty() && value.toInt() > PAYMENT_MUST_HIGHER_THAN_VALUE
                ),
            currentAmountError = if (value.isNotEmpty() && value.toInt() > maximumPayment) {
                Pair(true, R.string.payment_amount_amount_max_error)
            } else if (value.isNotEmpty() && value.toInt() <= PAYMENT_MUST_HIGHER_THAN_VALUE) {
                Pair(true, R.string.payment_amount_amount_min_error)
            } else {
                Pair(false, R.string.empty)
            }
        )
    }

    private fun onNavigateBack() = navigateBack(popTo = Screen.PaymentAccountScreen.route, isRestart = false)

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun onNavigateToVoucher() = navigateTo(
        route = "${Screen.PaymentVoucherScreen.baseRoute}/$user/$idBrand/$idClient/$idLoanClient/${encodeData(uiState.clientBankAccount)}/$paymentDate/${if (isMultiCurrency()) getMultiCurrencyAmountIncludingExchangeFormatted() else getCurrentAmountFormatted()}/${if (uiState.isMinimumSelected) uiState.minimumPaymentLabel else uiState.maximumPaymentLabel}/${formattedExchangeRateLabel()}/${shouldDisplayExchangeRate()}/${isMultiCurrency()}/${uiState.isAutomaticProgrammedPaymentChecked}/$referenceNumber"
    )

    private fun formattedExchangeRateLabel() = uiState.exchangeRateLabel.formattedTwoDecimalsNumber().toString()

    fun getFormattedCurrency() =
        if (uiState.currentAmountValueString.isNotEmpty() && uiState.currentAmountValueString.toInt() > maximumPayment) {
            uiState.maximumPaymentLabel
        } else {
            PAYMENT_MUST_HIGHER_THAN_VALUE
        }

    private fun onCallQueryGetExchangeRate() = executeUseCase {
        queryGetExchangeRateCreditUseCase.invoke(
            idBrand ?: NO_SELECT,
            user ?: "",
            identification ?: "",
            summaryList?.find { it.idCurrency != uiState.clientBankAccount?.idCurrency }?.idCurrency?.toString() ?: "",
            uiState.clientBankAccount?.idCurrency?.toString() ?: "",
            if (isMultiCurrency()) {
                summaryList?.find { it.idCurrency != uiState.clientBankAccount?.idCurrency }?.let {
                    if (uiState.isMinimumSelected) {
                        it.minPayment?.formattedTwoDecimalsNumber()
                    } else {
                        it.currentBalance?.formattedTwoDecimalsNumber()
                    }
                } ?: 0.0
            } else {
                uiState.currentAmountValueString.toDouble()
            }
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(
                    exchangeRateLabel = it?.result?.exchangeRate ?: 0.0,
                    exchangeConvertedAmount = it?.result?.convertedAmount ?: 0.0
                )
                onUIEvent(OnLoadingValueChange(false))
                onShowPaymentBottomSheet()
            }
            result.onLoading { onUIEvent(OnLoadingValueChange(true)) }
        }
    }

    private fun onProcessPayment(paymentDescription: String) =
        executeUseCase {
            onLoadingValueChange(true)
            mutationProcessPaymentUseCase.invoke(
                user = user ?: "",
                idBrand = idBrand ?: NO_SELECT,
                customerId = idClient ?: NO_SELECT,
                identification = identification ?: "",
                originAccountNumber = uiState.clientBankAccount?.accountNumber ?: "",
                destinyAccountNumber = getDestinyAccountNumber(uiState.clientBankAccount?.idCurrency),
                currencyId = uiState.clientBankAccount?.idCurrency?.toString() ?: "",
                customerName = userName ?: "",
                description = paymentDescription,
                destinyAccount = summaryList?.map {
                    DestinyAccount(
                        destinyAccountNumber = it.ibanAccount ?: "",
                        destinyCurrencyId = it.idCurrency?.toString() ?: NO_SELECT.toString(),
                        destinyAmount = if (isMultiCurrency()) {
                            if (uiState.isMinimumSelected) {
                                it.minPayment?.formattedTwoDecimalsNumber()
                            } else {
                                it.currentBalance?.formattedTwoDecimalsNumber()
                            }
                        } else {
                            uiState.currentAmountValueString.toDouble()
                        }
                    )
                } ?: listOf(),
                amount = if (isMultiCurrency()) {
                    getMultiCurrencyAmountIncludingExchangeValue()
                } else if (shouldDisplayExchangeRate()) {
                    getConvertedAmountValue()
                } else {
                    uiState.currentAmountValueString.toDouble()
                }
            ).collectLatest { result ->
                result.onSuccess {
                    referenceNumber = it?.referenceNumberSinpe
                    if (uiState.isAutomaticProgrammedPaymentChecked) {
                        onCallMutationActivateClientAutomaticDebitUseCase()
                    } else {
                        onUIEvent(OnHidePaymentBottomSheet)
                        onLoadingValueChange(false)
                        onUIEvent(OnNavigateToVoucher)
                    }
                }.onMessage {
                    onUIEvent(OnHidePaymentBottomSheet)
                    onLoadingValueChange(false)
                    uiState = uiState.copy(
                        alertResultTitle = it?.message ?: "",
                        alertResultDescription = it?.detail ?: "",
                        isAlertResultVisible = true
                    )
                }.onFailure {
                    onLoadingValueChange(false)
                    onUIEvent(OnHidePaymentBottomSheet)
                    uiState = uiState.copy(
                        alertResultTitle = it.getError() ?: "",
                        isAlertResultVisible = true
                    )
                }.onLoading {
                    onLoadingValueChange(true)
                }
            }
        }

    private fun onCallMutationActivateClientAutomaticDebitUseCase() = executeUseCase {
        mutationActivateClientAutomaticDebitUseCase.invoke(
            user = user.orEmpty(),
            idBrand = idBrand ?: 0,
            idClient = idClient?.toLong() ?: 0,
            idLoanClient = idLoanClient?.toLong() ?: 0,
            origin = uiState.clientBankAccount?.origin ?: "",
            idAccount = uiState.clientBankAccount?.id?.toLong() ?: 0,
            idCurrency = uiState.clientBankAccount?.idCurrency ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                onActivateClientAutomaticDebitResult(it?.isUpdated ?: false)
            }.onMessage {
                onActivateClientAutomaticDebitResult(false)
            }.onFailure {
                onActivateClientAutomaticDebitResult(false)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onActivateClientAutomaticDebitResult(isAutomaticProgrammed: Boolean) {
        onAutomaticProgrammedPaymentCheckedChanged(isAutomaticProgrammed)
        onUIEvent(OnHidePaymentBottomSheet)
        onLoadingValueChange(false)
        onUIEvent(OnNavigateToVoucher)
    }

    private fun getDestinyAccountNumber(idCurrency: Int?): String =
        summaryList?.find { it.idCurrency == idCurrency }?.ibanAccount ?: ""

    private fun onAutomaticProgrammedPaymentCheckedChanged(value: Boolean) {
        uiState = uiState.copy(isAutomaticProgrammedPaymentChecked = value)
    }

    private fun onAlertResultButtonClick() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun onLoadingValueChange(loading: Boolean) {
        uiState = uiState.copy(isLoading = loading)
    }

    fun getConvertedAmountFormatted() =
        "${uiState.accountCurrency}${
        uiState.exchangeConvertedAmount.formattedTwoDecimalsNumber().toString()
            .stringToDoubleFormat(CreditAmountViewModel.CURRENCY_SEPARATOR.toString())
        }"

    private fun getConvertedAmountValue() =
        uiState.exchangeConvertedAmount.formattedTwoDecimalsNumber()

    fun getCurrentAmountFormatted() =
        "${uiState.currency}${
        uiState.currentAmountValueString
            .stringToDoubleFormat(CreditAmountViewModel.CURRENCY_SEPARATOR.toString())
        }"

    fun getExchangeRateFormatted() =
        "${uiState.currency}${
        uiState.exchangeRateLabel.formattedTwoDecimalsNumber().toString()
            .stringToDoubleFormat(CreditAmountViewModel.CURRENCY_SEPARATOR.toString())
        }"

    private fun getMultiCurrencyAmountIncludingExchangeValue(): Double {
        val balance = if (uiState.isMinimumSelected) {
            summaryList?.find { it.idCurrency == uiState.clientBankAccount?.idCurrency }?.minPayment ?: 0.0
        } else {
            summaryList?.find { it.idCurrency == uiState.clientBankAccount?.idCurrency }?.currentBalance ?: 0.0
        }
        val exchangedAmount = uiState.exchangeConvertedAmount
        val total = balance + exchangedAmount
        return total.formattedTwoDecimalsNumber()
    }

    fun getMultiCurrencyAmountIncludingExchangeFormatted(): String {
        val balance = if (uiState.isMinimumSelected) {
            summaryList?.find { it.idCurrency == uiState.clientBankAccount?.idCurrency }?.minPayment ?: 0.0
        } else {
            summaryList?.find { it.idCurrency == uiState.clientBankAccount?.idCurrency }?.currentBalance ?: 0.0
        }
        val exchangedAmount = uiState.exchangeConvertedAmount
        val total = balance + exchangedAmount
        return "${uiState.accountCurrency}${
        total.formattedTwoDecimalsNumber().toString()
            .stringToDoubleFormat(CreditAmountViewModel.CURRENCY_SEPARATOR.toString())
        }"
    }

    data class UIState(
        // Interactions
        val minimumPaymentLabel: String = "",
        val maximumPaymentLabel: String = "",
        val isMinimumSelected: Boolean = false,
        val isMaximumSelected: Boolean = false,
        val currency: String = "$",
        val accountCurrency: String = "$",
        val currentAmountValueString: String = "0",
        val currentAmountError: Pair<Boolean, Int> = Pair(false, R.string.error_empty),
        val enableButton: Boolean = false,
        val isAmountVisible: Boolean = true,
        val isAutomaticProgrammedPaymentChecked: Boolean = false,
        val exchangeRateLabel: Double = 0.0,
        val exchangeConvertedAmount: Double = 0.0,
        val clientBankAccount: ClientBankAccount? = null,
        val isAlertResultVisible: Boolean = false,
        val alertResultTitle: String = "",
        val alertResultDescription: String = "",
        val isLoading: Boolean = false,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateBackHome -> onNavigateBackHome()
            is OnAmountValueChange -> onAmountValueChange(uiEvent.value)
            is OnMinimumPaymentButtonClick -> onAmountButtonClick(true)
            is OnMaximumPaymentButtonClick -> onAmountButtonClick(false)
            is OnPaymentButtonClick -> onPaymentButtonClick()
            is OnProcessPayment -> onProcessPayment(uiEvent.paymentDescription)
            is OnAutomaticProgrammedPaymentCheckedChanged -> onAutomaticProgrammedPaymentCheckedChanged(uiEvent.value)
            is OnAlertResultButtonClick -> onAlertResultButtonClick()
            is OnLoadingValueChange -> onLoadingValueChange(uiEvent.isLoading)
            is OnCallQueryGetExchangeRateCredit -> onCallQueryGetExchangeRate()
            is OnHidePaymentBottomSheet -> onHidePaymentBottomSheet()
            is OnNavigateToVoucher -> onNavigateToVoucher()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
        object OnMinimumPaymentButtonClick : UIEvent()
        object OnMaximumPaymentButtonClick : UIEvent()
        data class OnAmountValueChange(val value: String) : UIEvent()
        data class OnProcessPayment(val paymentDescription: String) : UIEvent()
        class OnAutomaticProgrammedPaymentCheckedChanged(val value: Boolean) : UIEvent()
        object OnPaymentButtonClick : UIEvent()
        object OnAlertResultButtonClick : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        object OnCallQueryGetExchangeRateCredit : UIEvent()
        object OnHidePaymentBottomSheet : UIEvent()
        object OnNavigateToVoucher : UIEvent()
    }

    companion object {
        const val NO_SELECT = -1
        const val PAYMENT_MUST_HIGHER_THAN_VALUE = 0
        const val PAYMENT_PLUS = " + "
    }
}
