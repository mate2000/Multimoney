package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontactamount

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.Brand.ElSalvador
import com.multimoney.domain.interaction.accountsmart.MutationProcessLocalTransferUseCase
import com.multimoney.domain.model.accountsmart.PhoneSmart
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.DESTINY_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ORIGIN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import com.multimoney.multimoney.presentation.util.SEPARATOR
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType.Dollar
import com.multimoney.multimoney.presentation.util.catalog.DisplayAccount
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class MyContactsTransferAmountViewModel @Inject constructor(
    private val processLocalTransfer: MutationProcessLocalTransferUseCase
) : BaseSmartEditAmountViewModel() {
    // stateless
    var fromSmartLabel: Int = R.string.smart_iban_transfer_smart_account_colon
    var totalBalanceLabel: String = ""
    var phoneAccount: PhoneSmart? = null

    override fun onStart() {
        viewModelScope.launch {
            initializeValues()
            smartAccount = savedStateHandle[ORIGIN_ACCOUNT]
            phoneAccount = savedStateHandle[DESTINY_ACCOUNT]
            originCurrency = smartAccount?.currencyID?.getCurrencyFromId()
            destinyCurrency = phoneAccount?.idCurrency?.getCurrencyFromId()
            shouldDisplayExchange = originCurrency != destinyCurrency

            amountUIState = amountUIState.copy(
                originAccountDisplay = DisplayAccount(
                    sheetLabel = R.string.smart_payment_amount_bottom_sheet_from,
                    sheetTitleResource = originCurrency?.myAccountSmartSymbol,
                    sheetSubtitle = if (idBrand == ElSalvador.id) null else getMaskedAccountIban(
                        smartAccount?.ibanAccountNumber.orEmpty()
                    ),
                    icon = R.drawable.ic_multimoney_smart
                ),
                destinyAccountDisplay = DisplayAccount(
                    sheetLabel = R.string.smart_payment_amount_bottom_sheet_to,
                    sheetTitle = phoneAccount?.titular,
                    sheetSubtitle = phoneAccount?.number?.plus(SEPARATOR)
                        ?.plus(destinyCurrency?.stringName)
                ),
                currency = destinyCurrency?.symbol ?: Dollar.symbol,
                placeholder = if (destinyCurrency == Dollar) {
                    R.string.smart_dollar_placeholder
                } else {
                    R.string.smart_colon_placeholder
                }
            )

            fromSmartLabel = if (originCurrency == CurrencyType.Colon) {
                R.string.smart_iban_transfer_smart_account_colon
            } else {
                R.string.smart_iban_transfer_smart_account_dolar
            }
            totalBalanceLabel = originCurrency?.symbol + smartAccount?.totalBalance.toString()
            getExchangeOnCompleted(
                true,
                abbreviation = originCurrency?.disbursementValue ?: "",
                idOriginCurrency = destinyCurrency?.id.toString(),
                idDestinationCurrency = originCurrency?.id.toString()
            )
        }
    }

    override fun onProcessTransfer() {
        if (idBrand == Brand.CostaRica.id) {
            onCallProcessSinpeTransfer(
                originIdentification = identification,
                originAccountNumber = smartAccount?.ibanAccountNumber.orEmpty(),
                originCustomerName = userName,
                originCurrency = originCurrency?.id.toString(),
                destinationCustomerName = phoneAccount?.titular.orEmpty(),
                destinationAccountNumber = phoneAccount?.ibanNumber.orEmpty(),
                destinationCurrency = destinyCurrency?.id.toString(),
                destinationIdentification = phoneAccount?.identification.orEmpty(),
                transferType = SmartSinpeTransferType.SEND
            )
        } else if (idBrand == Brand.ElSalvador.id) {
            onProcessLocalTransfer()
        }
    }

    private fun onProcessLocalTransfer() {
        executeUseCase {
            processLocalTransfer.invoke(
                pkUsuario = pkUser.toIntOrNull() ?: 0,
                user = userName,
                idBrand = idBrand,
                originIdentification = identification,
                idCurrencyOrigin = originCurrency?.currency ?: "",
                destinationIdentification = phoneAccount?.identification ?: "",
                idCurrencyDestination = destinyCurrency?.currency ?: "",
                destinationAccountNumber = phoneAccount?.accountNumber ?: "",
                amount = amountUIState.currentAmountValueString?.toDoubleOrNull() ?: 0.0,
                reason = amountUIState.motive,
                accountToken = smartAccount?.tokenAccount?.toLongOrNull() ?: 0,
                exchangeRate = amountUIState.exchangeRate
            ).collectLatest { result ->
                result.onSuccess {
                    amountUIState = amountUIState.copy(
                        currentDate = getCurrentDate(Calendar.getInstance().time),
                        currentTime = getCurrentTime(Calendar.getInstance().time).lowercase(),
                        referenceNumber = it?.authorization ?: "",
                        showLoadingScreen = false,
                        paymentSuccess = true,
                        showErrorScreen = false
                    )
                }
                result.onLoading {
                    amountUIState = amountUIState.copy(
                        showLoadingScreen = true,
                        paymentSuccess = false,
                        showErrorScreen = false
                    )
                }
                result.onFailure {
                    amountUIState = amountUIState.copy(
                        showLoadingScreen = false,
                        paymentSuccess = false,
                        showErrorScreen = true
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

    override fun onAmountCompleted() {
        if (shouldDisplayExchange) {
            getExchangeOnCompleted(
                abbreviation = originCurrency?.disbursementValue ?: "",
                idOriginCurrency = destinyCurrency?.id.toString(),
                idDestinationCurrency = originCurrency?.id.toString()
            )
        } else {
            validateAmount()
        }
    }

    override fun onContinueClick() {
        amountUIState = amountUIState.copy(
            bottomSheetState = ModalBottomSheetState(Expanded)
        )
    }

    override fun onNavigateBack() {
        navigateBack(popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false)
    }
}
