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
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnAbandonFlow
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnMotiveChange
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnTryLater
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.SmartAmountBody
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun OwnTransferAmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: OwnTransferAmountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
            onBaseUIEvent(OnStart)
        }
    }

    if (viewModel.baseUIState.showLoadingScreen) {
        LoadingMultiMoney(R.string.smart_processing_transaction)
        // This else if will be managed in other ticket
    } else if (viewModel.baseUIState.showErrorScreen) {
        val notificationTitle = stringResource(R.string.smart_saving_try_later_notification_title)
        val notificationBody = stringResource(R.string.smart_saving_try_later_notification_body)
        AlertResult(
            isTopNavBarVisible = false,
            titleResource = R.string.error_occurred_title,
            descriptionResource = R.string.error_please_try_again,
            buttonTextResource = R.string.error_button_retry,
            onButtonClick = { viewModel.onBaseUIEvent(OnRetryTransfer) },
            isSecondaryButtonVisible = true,
            secondaryButtonTextResource = R.string.error_button_try_later,
            onSecondaryButtonClick = {
                viewModel.onBaseUIEvent(
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
            viewModel.onBaseUIEvent(OnNavigateHome)
        }
    } else if (viewModel.baseUIState.paymentSuccess) {
        // Todo add success screen
        BackHandler {
            viewModel.onBaseUIEvent(OnNavigateHome)
        }
    } else {
        OwnTransferAmountContent(viewModel)
        // Todo add confirmation sheet
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
                    viewModel.maskedCardNumber,
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
            exchangeRate = viewModel.baseUIState.exchangeRateLabel,
            convertedTotal = viewModel.baseUIState.convertedAmountLabel,
            shouldDisplayExchange = viewModel.shouldDisplayExchange,
            onContinueClick = { viewModel.onBaseUIEvent(OnContinueClick) },
            enableButton = viewModel.baseUIState.enableButton,
            motive = viewModel.baseUIState.motive,
            onMotiveChange = { viewModel.onBaseUIEvent(OnMotiveChange(it)) }
        )
    }
}