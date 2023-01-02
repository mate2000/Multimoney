package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount

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
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountViewModel.UIEvent.OnAmountValueChange
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
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.CARD_NUMBER_LAST_DIGITS
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
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
        // Todo add success screen
        BackHandler {
            viewModel.onUIEvent(OnNavigateHome)
        }
    } else {
        SmartTransferAmountContent(viewModel)
        // Todo add confirmation sheet
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