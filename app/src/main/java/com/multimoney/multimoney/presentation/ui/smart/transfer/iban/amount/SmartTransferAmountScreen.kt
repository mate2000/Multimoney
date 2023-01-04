package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnAbandonFlow
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnCallProcessSinpeTransfer
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnMotiveChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.SmartAmountBody
import com.multimoney.multimoney.presentation.uielement.SmartPaymentBottomSheet
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getFullMaskedAccountIban
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun SmartTransferAmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartTransferAmountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
            onUIEvent(OnStart)
        }
    }

    if (viewModel.uiState.showLoadingScreen) {
        LoadingMultiMoney(R.string.smart_processing_transaction)
    } else if (viewModel.uiState.showErrorScreen) {
        AlertResult(
            titleResource = R.string.error_occurred_title,
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateHome) },
            descriptionResource = R.string.error_try_again,
            buttonTextResource = R.string.error_button_try_again,
            onButtonClick = { viewModel.onUIEvent(OnRetryTransfer) }
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

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onDismissAction = viewModel.uiState.openDialog.dismissAction,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction
        )
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
            isRightButtonVisible = true,
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(OnAbandonFlow) }
        )
        SmartAmountBody(
            titleId = R.string.smart_iban_transfer_send_money,
            originAccountSubtitle = stringResource(
                id = viewModel.fromSmartLabel,
                getMaskedAccountIban(
                    viewModel.editAmountHelper.maskedCardNumber,
                    stringResource(id = R.string.payment_account_masked_text)
                )
            ),
            currentAmount = viewModel.uiState.currentAmountValueString,
            amountPlaceHolderId = viewModel.uiState.placeholder,
            onAmountChange = {
                viewModel.onUIEvent(OnAmountValueChange(it))
            },
            onDebounceValidation = { viewModel.onUIEvent(OnAmountCompleted(it)) },
            isAmountError = viewModel.uiState.isAmountValid.not(),
            amountErrorMessage = stringResource(
                id = R.string.smart_iban_transfer_error_balance_insufficient,
                viewModel.totalBalanceLabel
            ),
            currency = viewModel.uiState.currency,
            exchangeRate = viewModel.uiState.exchangeRateLabel,
            convertedTotal = viewModel.uiState.convertedAmountLabel,
            shouldDisplayExchange = viewModel.editAmountHelper.shouldDisplayExchange,
            onContinueClick = { viewModel.onUIEvent(OnContinueClick) },
            enableButton = viewModel.uiState.enableButton,
            motive = viewModel.uiState.motive,
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
        amount = viewModel.getFormattedAmount(),
        exchangedAmount = if (viewModel.editAmountHelper.shouldDisplayExchange) viewModel.uiState.convertedAmountLabel else null,
        fromLabel = stringResource(R.string.smart_payment_sheet_from_account),
        fromIcon = R.drawable.ic_multimoney_smart,
        fromTitle = stringResource(
            R.string.smart_payment_amount_bottom_sheet_my_smart_account,
            viewModel.editAmountHelper.smartCurrency?.symbol ?: ""
        ),
        fromSubtitle = getMaskedAccountIban(
            viewModel.editAmountHelper.maskedCardNumber,
            stringResource(R.string.payment_account_masked_text)
        ),
        toLabel = stringResource(R.string.smart_payment_sheet_to_account),
        toIcon = R.drawable.ic_bank_account_dollar,
        toTitle = viewModel.editAmountHelper.accountName,
        toSubtitle = getFullMaskedAccountIban(
            viewModel.editAmountHelper.bankDetail,
            viewModel.editAmountHelper.smartAccount?.ibanAccountNumber ?: "",
            stringResource(R.string.payment_account_masked_text)
        ),
        motive = viewModel.uiState.motive,
        buttonText = stringResource(R.string.payment_amount_bottom_sheet_send_button),
        buttonAction = { viewModel.onUIEvent(OnCallProcessSinpeTransfer) }
    )
}
