package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountViewModel.UIEvent.OnAbandonFlow
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountViewModel.UIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountViewModel.UIEvent.OnMotiveChange
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountViewModel.UIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountViewModel.UIEvent.OnTryLater
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.SmartAmountBody
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun OwnTransferAmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: OwnTransferAmountViewModel = hiltViewModel()
){
    val context = LocalContext.current
    LaunchedEffect( true ) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
            onUIEvent(OnStart)
        }
    }

    if (viewModel.uiState.showLoadingScreen) {
        LoadingMultiMoney(R.string.smart_processing_transaction)
        // This else if will be managed in other ticket
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
        // Todo add success screen
        BackHandler {
            viewModel.onUIEvent(OnNavigateHome)
        }
    } else {
        OwnTransferAmountContent(viewModel)
        // Todo add confirmation sheet
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
fun OwnTransferAmountContent(viewModel: OwnTransferAmountViewModel = hiltViewModel()) {
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