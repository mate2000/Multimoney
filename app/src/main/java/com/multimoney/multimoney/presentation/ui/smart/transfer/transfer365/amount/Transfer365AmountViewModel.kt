package com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.amount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.accountsmart.MutationProcessTransfer365MobileUseCase
import java.util.Calendar
import com.multimoney.domain.interaction.accountsmart.MutationProcessTransfer365UseCase
import com.multimoney.domain.model.accountsmart.Transfer365Account
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.DESTINY_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ORIGIN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import com.multimoney.multimoney.presentation.util.catalog.DisplayAccount
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedSmartAccount
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class Transfer365AmountViewModel @Inject constructor(
    private val processTransfer365UseCase: MutationProcessTransfer365UseCase,
    private val processTransfer365MobileUseCase: MutationProcessTransfer365MobileUseCase
) : BaseSmartEditAmountViewModel() {

    // stateless
    var fromSmartLabel: Int = R.string.transfer_365_amount_from_label
    var totalBalanceLabel: String = ""
    var transfer365Account = Transfer365Account()

    override fun onStart() {
        viewModelScope.launch {
            initializeValues()
            transfer365Account = savedStateHandle[DESTINY_ACCOUNT] ?: Transfer365Account()
            smartAccount = savedStateHandle[ORIGIN_ACCOUNT]
            originCurrency = smartAccount?.currencyID?.getCurrencyFromId() ?: CurrencyType.Dollar
            if (originCurrency == CurrencyType.All) originCurrency = CurrencyType.Dollar
            shouldDisplayExchange = false

            amountUIState = amountUIState.copy(
                originAccountDisplay = DisplayAccount(
                    sheetLabel = R.string.transfer_365_pre_confirmation_from_label,
                    sheetTitleResource = originCurrency?.myAccountSmartName,
                    sheetSubtitleResource = R.string.empty,
                    icon = R.drawable.ic_multimoney_smart
                ),
                currency = originCurrency?.symbol ?: CurrencyType.Dollar.symbol,
                placeholder = if (originCurrency == CurrencyType.Dollar) {
                    R.string.smart_dollar_placeholder
                } else {
                    R.string.empty
                }
            )
            totalBalanceLabel =
                amountUIState.currency + smartAccount?.totalBalance.toString()
            val destinationInfo = when (transferType) {
                SmartTransferTypes.SmartToMobile.id -> {
                    transfer365Account.phone
                }
                SmartTransferTypes.SmartToOtherBank.id -> {
                    "${transfer365Account.bankName} | ${
                        getMaskedSmartAccount(
                            prefix = "",
                            accountNumber = transfer365Account.accountNumber.orEmpty()
                        )
                    }"
                }
                else -> {
                    ""
                }
            }
            amountUIState = amountUIState.copy(
                destinyAccountDisplay = DisplayAccount(
                    sheetLabel = R.string.smart_payment_amount_bottom_sheet_to,
                    sheetTitle = "${transfer365Account.name} ${transfer365Account.lastname}",
                    sheetSubtitle = destinationInfo,
                    icon = R.drawable.ic_bank_account_dollar
                )
            )
        }
    }

    override fun onContinueClick() {
        amountUIState = amountUIState.copy(
            bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
        )
    }

    override fun onAmountChanged(newAmount: String) {
        if (validateDecimalIncome(newAmount)) {
            amountUIState = amountUIState.copy(
                currentAmountValueString = newAmount
            )
        }
    }

    override fun onAmountCompleted() {
        val amount = amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0
        amountUIState =
            amountUIState.copy(isAmountValid = amount <= (smartAccount?.totalBalance ?: 0.0))
        amountUIState = amountUIState.copy(enableButton = validateForm())
    }

    override fun validateForm(
        newAmount: String?,
        newMotive: String,
        isAmountValid: Boolean
    ): Boolean {
        return when {
            newAmount?.isEmpty() == true -> false
            (newAmount?.toDoubleOrNull() ?: 0.0) <= 0.0 -> false
            newMotive.isEmpty() -> false
            amountUIState.isAmountValid.not() -> false
            else -> true
        }
    }

    private fun processTransfer365() {
        executeUseCase {
            processTransfer365UseCase.invoke(
                identification = identification,
                destinationAccount = transfer365Account.accountNumber.orEmpty(),
                destinationBankId = transfer365Account.bankId,
                destinationType = transfer365Account.destinationType.orEmpty(),
                typeAccountId = transfer365Account.accountTypeId,
                destinationName = transfer365Account.name,
                destinationLastName = transfer365Account.lastname,
                amount = amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0,
                motive = amountUIState.motive,
                user = pkUser,
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { reference ->
                    if (reference?.referenceNumber.isNullOrBlank()) {
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
                            referenceNumber = reference?.referenceNumber ?: ""
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

    private fun processTransfer365Mobile() {
        executeUseCase {
            processTransfer365MobileUseCase.invoke(
                identification = identification,
                phoneNumber = transfer365Account.phone.orEmpty(),
                destinationBankId = transfer365Account.bankId,
                typeAccountId = transfer365Account.accountTypeId,
                destinationName = transfer365Account.name,
                destinationLastName = transfer365Account.lastname,
                amount = amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0,
                motive = amountUIState.motive,
                user = pkUser,
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { reference ->
                    if (reference?.referenceNumber.isNullOrBlank()) {
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
                            referenceNumber = reference?.referenceNumber ?: ""
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

    override fun onProcessTransfer() {
        if (transferType == SmartTransferTypes.SmartToOtherBank.id) {
            processTransfer365()
        } else if (transferType == SmartTransferTypes.SmartToMobile.id) {
            processTransfer365Mobile()
        }
    }

    override fun onNavigateBack() {
        // Todo add validation to go back to list transfer 365 accounts screen
        val screen = when (previousScreen) {
            Screen.SmartAdd365AccountScreen.baseRoute -> Screen.SmartAdd365AccountScreen.route
            else -> Screen.HomeScreen.route
        }
        navigateBack(popTo = screen, isRestart = false)
    }
}