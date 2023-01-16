package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount

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
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnAbandonFlow
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnCallProcessTransfer
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnMotiveChange
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.SmartAmountBody
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.SmartPaymentBottomSheet
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun OwnTransferAmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: OwnTransferAmountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
            onBaseUIEvent(OnStart)
        }
    }

    if (viewModel.baseUIState.showLoadingScreen) {
        LoadingMultiMoney(R.string.smart_processing_transaction)
    } else if (viewModel.baseUIState.showErrorScreen) {
        AlertResult(
            isTopNavBarVisible = false,
            titleResource = R.string.error_occurred_title,
            descriptionResource = R.string.error_try_again,
            buttonTextResource = R.string.error_button_try_again,
            onButtonClick = { viewModel.onBaseUIEvent(OnRetryTransfer) }
        )
        BackHandler {
            viewModel.onBaseUIEvent(OnNavigateHome)
        }
    } else if (viewModel.baseUIState.paymentSuccess) {
        OwnTransferSuccessScreen(viewModel)
        BackHandler {
            viewModel.onBaseUIEvent(OnNavigateHome)
        }
    } else {
        OwnTransferAmountContent(viewModel)
        OwnTransferAmountBottomSheet(viewModel)
        BackHandler {
            viewModel.onBaseUIEvent(OnNavigateBack)
        }
    }

    if (viewModel.baseUIState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.baseUIState.openDialog.titleResource),
            message = stringResource(id = viewModel.baseUIState.openDialog.descriptionResource).ifEmpty { viewModel.baseUIState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.baseUIState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.baseUIState.openDialog.negativeResource),
            openDialogCustom = viewModel.baseUIState.openDialog.isActive,
            onDismissAction = viewModel.baseUIState.openDialog.dismissAction,
            onNegativeAction = viewModel.baseUIState.openDialog.negativeAction
        )
    }

    LoadingIndicator(viewModel.baseUIState.isLoading)
}

@Composable
@Preview
fun OwnTransferAmountContent(viewModel: OwnTransferAmountViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isRightButtonVisible = true,
            onLeftButtonClick = { viewModel.onBaseUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onBaseUIEvent(OnAbandonFlow) }
        )
        SmartAmountBody(
            titleId = R.string.smart_iban_transfer_send_money,
            originAccountSubtitle = stringResource(
                id = viewModel.fromSmartLabel,
                getMaskedAccountIban(
                    viewModel.smartAccount?.ibanAccountNumber ?: "",
                    stringResource(id = R.string.payment_account_masked_text)
                )
            ),
            currentAmount = viewModel.baseUIState.currentAmountValueString,
            amountPlaceHolderId = viewModel.baseUIState.placeholder,
            onAmountChange = {
                viewModel.onBaseUIEvent(OnAmountValueChange(it))
            },
            onDebounceValidation = { viewModel.onBaseUIEvent(OnAmountCompleted(it)) },
            isAmountError = viewModel.baseUIState.isAmountValid.not(),
            amountErrorMessage = stringResource(
                id = R.string.smart_iban_transfer_error_balance_insufficient,
                viewModel.totalBalanceLabel
            ),
            currency = viewModel.baseUIState.currency,
            exchangeRate = viewModel.baseUIState.exchangeRateLabel ?: "",
            convertedTotal = viewModel.baseUIState.convertedAmountLabel ?: "",
            shouldDisplayExchange = viewModel.shouldDisplayExchange,
            onContinueClick = { viewModel.onBaseUIEvent(OnContinueClick) },
            enableButton = viewModel.baseUIState.enableButton,
            motive = viewModel.baseUIState.motive,
            onMotiveChange = { viewModel.onBaseUIEvent(OnMotiveChange(it)) }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OwnTransferAmountBottomSheet(viewModel: OwnTransferAmountViewModel) {
    SmartPaymentBottomSheet(
        coroutineScope = rememberCoroutineScope(),
        modalBottomSheetState = viewModel.baseUIState.bottomSheetState,
        saveSendTitleResource = R.string.smart_payment_sheet_send_title,
        amount = viewModel.getFormattedAmount(),
        exchangedAmount = viewModel.baseUIState.convertedAmountLabel,
        fromLabel = stringResource(viewModel.baseUIState.originAccountDisplay?.sheetLabel ?: R.string.empty),
        fromTitle = viewModel.baseUIState.originAccountDisplay?.sheetTitle
            ?: stringResource(viewModel.baseUIState.originAccountDisplay?.sheetTitleResource ?: R.string.empty),
        fromSubtitle = viewModel.baseUIState.originAccountDisplay?.sheetSubtitle
            ?: stringResource(viewModel.baseUIState.originAccountDisplay?.sheetTitleResource ?: R.string.empty),
        fromIcon = viewModel.baseUIState.originAccountDisplay?.icon ?: R.drawable.ic_dropdown_open,
        toLabel = stringResource(R.string.smart_payment_amount_bottom_sheet_to),
        toTitle = viewModel.baseUIState.destinyAccountDisplay?.sheetTitle
            ?: stringResource(
                viewModel.baseUIState.destinyAccountDisplay?.sheetTitleResource ?: R.string.empty
            ),
        toSubtitle = viewModel.baseUIState.destinyAccountDisplay?.sheetSubtitle
            ?: stringResource(
                viewModel.baseUIState.destinyAccountDisplay?.sheetTitleResource ?: R.string.empty
            ),
        toIcon = viewModel.baseUIState.destinyAccountDisplay?.icon ?: R.drawable.ic_dropdown_open,
        buttonText = stringResource(string.button_continue),
        buttonAction = { viewModel.onBaseUIEvent(OnCallProcessTransfer) }
    )
}
