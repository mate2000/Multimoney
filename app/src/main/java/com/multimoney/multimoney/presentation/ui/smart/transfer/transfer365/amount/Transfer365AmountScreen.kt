package com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnAbandonFlow
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnCallProcessTransfer
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnMotiveChange
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.AmountUIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.SmartAmountBody
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.SmartPaymentBottomSheet
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccount

@Composable
fun Transfer365AmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: Transfer365AmountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
            onAmountUIEvent(OnStart)
        }
    }

    if (viewModel.amountUIState.showLoadingScreen) {
        LoadingMultiMoney(R.string.smart_processing_transaction)
    } else if (viewModel.amountUIState.showErrorScreen) {
        AlertResult(
            isTopNavBarVisible = true,
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onAmountUIEvent(OnNavigateHome) },
            titleResource = R.string.error_occurred_title,
            descriptionResource = R.string.error_try_again,
            buttonTextResource = R.string.error_button_try_again,
            onButtonClick = { viewModel.onAmountUIEvent(OnRetryTransfer) }
        )
        BackHandler {
            viewModel.onAmountUIEvent(OnNavigateHome)
        }
    } else if (viewModel.amountUIState.paymentSuccess) {
        Transfer365SuccessScreen(viewModel)
        BackHandler {
            viewModel.onAmountUIEvent(OnNavigateHome)
        }
    } else {
        Transfer365AmountContent(viewModel)
        Transfer365AmountBottomSheet(viewModel)
        BackHandler {
            viewModel.onAmountUIEvent(OnNavigateBack)
        }
    }

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

@Composable
fun Transfer365AmountContent(viewModel: Transfer365AmountViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onAmountUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onAmountUIEvent(OnAbandonFlow) }
        )
        SmartAmountBody(
            titleId = R.string.transfer_365_amount_title,
            originAccountSubtitle = stringResource(
                id = viewModel.fromSmartLabel,
                getMaskedAccount(
                    accountNumber = viewModel.smartAccount?.accountNumber.orEmpty(),
                    prefix = Brand.ElSalvador.countryCode.uppercase()
                )
            ),
            currentAmount = viewModel.amountUIState.currentAmountValueString,
            amountPlaceHolderId = viewModel.amountUIState.placeholder,
            onAmountChange = {
                viewModel.onAmountUIEvent(OnAmountValueChange(it))
            },
            onDebounceValidation = { viewModel.onAmountUIEvent(OnAmountCompleted(it)) },
            isAmountError = viewModel.amountUIState.isAmountValid.not(),
            amountErrorMessage = stringResource(
                id = R.string.smart_iban_transfer_error_balance_insufficient,
                viewModel.totalBalanceLabel
            ),
            currency = viewModel.amountUIState.currency,
            shouldDisplayExchange = viewModel.shouldDisplayExchange,
            onContinueClick = { viewModel.onAmountUIEvent(OnContinueClick) },
            enableButton = viewModel.amountUIState.enableButton,
            motive = viewModel.amountUIState.motive,
            onMotiveChange = { viewModel.onAmountUIEvent(OnMotiveChange(it)) },
            disclaimerResource = R.string.transfer_365_amount_disclaimer
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Transfer365AmountBottomSheet(viewModel: Transfer365AmountViewModel = hiltViewModel()) {
    SmartPaymentBottomSheet(
        coroutineScope = rememberCoroutineScope(),
        modalBottomSheetState = viewModel.amountUIState.bottomSheetState,
        saveSendTitleResource = R.string.smart_payment_sheet_send_title,
        amount = viewModel.getFormattedAmount(),
        fromTitle = viewModel.amountUIState.originAccountDisplay?.sheetTitle
            ?: stringResource(
                viewModel.amountUIState.originAccountDisplay?.sheetTitleResource ?: R.string.empty
            ),
        fromSubtitle = viewModel.amountUIState.originAccountDisplay?.sheetSubtitle
            ?: stringResource(
                viewModel.amountUIState.originAccountDisplay?.sheetSubtitleResource
                    ?: R.string.empty
            ),
        fromIcon = viewModel.amountUIState.originAccountDisplay?.icon,
        toTitle = viewModel.amountUIState.destinyAccountDisplay?.sheetTitle ?: stringResource(
            viewModel.amountUIState.destinyAccountDisplay?.sheetTitleResource ?: R.string.empty
        ),
        toSubtitle = viewModel.amountUIState.destinyAccountDisplay?.sheetSubtitle
            ?: stringResource(
                viewModel.amountUIState.destinyAccountDisplay?.sheetSubtitleResource
                    ?: R.string.empty
            ),
        toIcon = viewModel.amountUIState.destinyAccountDisplay?.icon,
        titleIcon = if (viewModel.transfer365Account.isFavorite) R.drawable.ic_star_filled else null,
        motive = viewModel.amountUIState.motive,
        buttonText = stringResource(R.string.payment_amount_bottom_sheet_send_button),
        buttonAction = { viewModel.onAmountUIEvent(OnCallProcessTransfer) }
    )
}
