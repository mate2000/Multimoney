package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnCallProcessSinpeTransfer
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnMotiveChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnTryLater
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.SmartAmountContent
import com.multimoney.multimoney.presentation.uielement.SmartPaymentBottomSheet
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.CARD_NUMBER_LAST_DIGITS
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.getFullMaskedAccountIban
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun SmartTransferAmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartTransferAmountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
            onUIEvent(OnStart)
        }
    }

    if (viewModel.uiState.showLoadingScreen) {
        LoadingMultiMoney(R.string.smart_processing_transaction)
    } else if (viewModel.uiState.showErrorScreen) {
        val notificationTitle = stringResource(R.string.smart_saving_try_later_notification_title)
        val notificationBody = stringResource(R.string.smart_saving_try_later_notification_body)
        AlertResult(
            isTopNavBarVisible = false,
            titleResource = R.string.error_occurred_title,
            descriptionResource = R.string.error_please_try_again,
            buttonTextResource = R.string.error_button_retry,
            onButtonClick = { viewModel.onUIEvent(OnRetryTransfer) },
            isSecondaryButtonVisible = true,
            secondaryButtonTextResource = R.string.error_button_try_later,
            onSecondaryButtonClick = {
                viewModel.onUIEvent(
                    OnTryLater(
                        notificationTitle,
                        notificationBody,
                        R.drawable.ic_logo_multimoney,
                        context
                    )
                )
            }
        )
        BackHandler {
            viewModel.onUIEvent(OnNavigateHome)
        }
    } else if (viewModel.uiState.paymentSuccess) {
        SmartTransferSuccessScreen(viewModel)
        BackHandler {
            viewModel.onUIEvent(OnNavigateHome)
        }
    } else {
        SmartTransferAmountContent(viewModel)
        SmartTransferBottomSheet(viewModel)
        BackHandler {
            viewModel.onUIEvent(OnNavigateBack)
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
@Preview
fun SmartTransferAmountContent(viewModel: SmartTransferAmountViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        )
        SmartAmountContent(
            titleId = R.string.smart_iban_transfer_send_money,
            originAccountSubtitle = stringResource(
                id = viewModel.fromSmartLabel,
                if (viewModel.editAmountHelper.smartCurrency == CurrencyType.Colon) {
                    getMaskedAccountIban(
                        viewModel.editAmountHelper.maskedCardNumber,
                        stringResource(id = R.string.payment_account_masked_text)
                    )
                } else {
                    stringResource(
                        R.string.visa_card_masked_number,
                        viewModel.editAmountHelper.maskedCardNumber.takeLast(CARD_NUMBER_LAST_DIGITS)
                    )
                }
            ),
            currentAmount = viewModel.uiState.currentAmountValueString,
            amountPlaceHolderId = viewModel.uiState.placeholder,
            onAmountChange = {
                viewModel.onUIEvent(OnAmountValueChange(it))
            },
            onDebounceValidation = { viewModel.onUIEvent(OnAmountCompleted(it)) },
            currency = viewModel.uiState.currency,
            shouldDisplayExchange = viewModel.editAmountHelper.shouldDisplayExchange,
            onContinueClick = { viewModel.onUIEvent(OnContinueClick) },
            enableButton = viewModel.uiState.enableButton,
            onMotiveChange = { viewModel.onUIEvent(OnMotiveChange(it)) }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SmartTransferBottomSheet(viewModel: SmartTransferAmountViewModel) {
    SmartPaymentBottomSheet(
        coroutineScope = rememberCoroutineScope(),
        modalBottomSheetState = viewModel.uiState.bottomSheetState,
        saveSendTitleResource = R.string.smart_payment_sheet_title,
        amount = viewModel.uiState.currentAmountValueString,
        exchangedAmount = if (viewModel.editAmountHelper.shouldDisplayExchange) viewModel.uiState.exchangeRateLabel else null,
        fromLabel = stringResource(R.string.smart_payment_sheet_from_account),
        fromTitle = stringResource(
            R.string.smart_payment_amount_bottom_sheet_my_smart_account,
            viewModel.editAmountHelper.smartCurrency?.symbol ?: ""
        ),
        fromSubtitle = stringResource(
            R.string.iban_masked_account_number,
            viewModel.editAmountHelper.smartAccount?.ibanAccountNumber ?: ""
        ),
        fromIcon = R.drawable.ic_multimoney_smart,
        toLabel = stringResource(R.string.smart_payment_sheet_to_account),
        toTitle = viewModel.editAmountHelper.ibanAccount?.nameAccount ?: "",
        toSubtitle = getFullMaskedAccountIban(
            viewModel.editAmountHelper.ibanAccount?.bank ?: "",
            viewModel.editAmountHelper.ibanAccount?.sinpeAccount ?: "",
            stringResource(R.string.payment_account_masked_text)
        ),
        toIcon = R.drawable.ic_bank_account_dollar,
        motive = "Pago de producto", // todo viewModel.uiState.motive,
        buttonText = stringResource(R.string.payment_amount_bottom_sheet_send_button),
        buttonAction = { viewModel.onUIEvent(OnCallProcessSinpeTransfer) }
    )
}
