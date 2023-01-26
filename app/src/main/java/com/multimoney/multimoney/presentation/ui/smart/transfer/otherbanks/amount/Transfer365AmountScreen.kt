package com.multimoney.multimoney.presentation.ui.smart.transfer.otherbanks.amount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.SmartAmountBody
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccount

@Composable
fun Transfer365AmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: Transfer365AmountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
        }
    }

    Transfer365AmountContent(viewModel)

    if (viewModel.amountUIState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.amountUIState.openDialog.titleResource),
            message = stringResource(id = viewModel.amountUIState.openDialog.descriptionResource).ifEmpty { viewModel.amountUIState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.amountUIState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.amountUIState.openDialog.negativeResource),
            openDialogCustom = viewModel.amountUIState.openDialog.isActive,
            onDismissAction = viewModel.amountUIState.openDialog.dismissAction,
            onNegativeAction = viewModel.amountUIState.openDialog.negativeAction
        )
    }

    LoadingIndicator(viewModel.amountUIState.isLoading)
}

@Composable fun Transfer365AmountContent(viewModel: Transfer365AmountViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { viewModel.onAmountUIEvent(BaseSmartEditAmountViewModel.AmountUIEvent.OnNavigateBack) }
        )
        SmartAmountBody(
            titleId = R.string.smart_saving_amount_title,
            originAccountSubtitle = stringResource(
                id = viewModel.fromSmartLabel,
                getMaskedAccount(
                    viewModel.smartAccount?.accountNumber.orEmpty(),
                    stringResource(id = R.string.payment_account_masked_text)
                )
            ),
            currentAmount = viewModel.amountUIState.currentAmountValueString,
            amountPlaceHolderId = viewModel.amountUIState.placeholder,
            onAmountChange = {
                viewModel.onAmountUIEvent(OnAmountValueChange(it))
            },
            onDebounceValidation = { viewModel.onAmountUIEvent(OnAmountCompleted(it)) },
            currency = viewModel.amountUIState.currency,
            exchangeRate = viewModel.amountUIState.exchangeRateLabel.orEmpty(),
            convertedTotal = viewModel.amountUIState.convertedAmountLabel.orEmpty(),
            shouldDisplayExchange = viewModel.shouldDisplayExchange,
            onContinueClick = { viewModel.onAmountUIEvent(OnContinueClick) },
            enableButton = viewModel.amountUIState.enableButton,
            disclaimerResource = R.string.transfer_365_amount_disclaimer
        )
    }
}