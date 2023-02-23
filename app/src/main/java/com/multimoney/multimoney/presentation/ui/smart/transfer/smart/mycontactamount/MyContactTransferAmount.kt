package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.mycontactamount

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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
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
import com.multimoney.multimoney.presentation.uielement.CustomContactIcon
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.SmartPaymentBottomSheet
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccount
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun MyContactsTransferAmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: MyContactsTransferAmountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
            onAmountUIEvent(OnStart)
        }
    }

    if (viewModel.amountUIState.showLoadingScreen) {
        LoadingMultiMoney(string.smart_processing_transaction)
    } else if (viewModel.amountUIState.showErrorScreen) {
        AlertResult(
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onAmountUIEvent(OnNavigateHome) },
            titleString = viewModel.amountUIState.errorMessage,
            descriptionString = viewModel.amountUIState.errorDetail,
            buttonTextResource = string.error_button_try_again,
            onButtonClick = { viewModel.onAmountUIEvent(OnRetryTransfer) }
        )

        BackHandler {
            viewModel.onAmountUIEvent(OnNavigateHome)
        }
    } else if (viewModel.amountUIState.paymentSuccess) {
        MyContactsTransferSuccess(viewModel)
        BackHandler { viewModel.onAmountUIEvent(OnNavigateHome) }
    } else {
        MyContactsTransferAmountContent(viewModel)
        MyContactsAmountBottomSheet(viewModel)
        BackHandler { viewModel.onAmountUIEvent(OnNavigateBack) }
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
@Preview
fun MyContactsTransferAmountContent(viewModel: MyContactsTransferAmountViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isRightButtonVisible = true,
            onLeftButtonClick = { viewModel.onAmountUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onAmountUIEvent(OnAbandonFlow) }
        )
        SmartAmountBody(
            titleId = string.smart_iban_transfer_send_money,
            originAccountSubtitle = stringResource(
                id = viewModel.fromSmartLabel,
                if (viewModel.idBrand == Brand.CostaRica.id) {
                    getMaskedAccountIban(
                        viewModel.smartAccount?.ibanAccountNumber.orEmpty()
                    )
                } else {
                    getMaskedAccount(
                        accountNumber = viewModel.smartAccount?.accountNumber.orEmpty(),
                        prefix = ""
                    )
                }
            ),
            currentAmount = viewModel.amountUIState.currentAmountValueString,
            amountPlaceHolderId = viewModel.amountUIState.placeholder,
            onAmountChange = {
                viewModel.onAmountUIEvent(OnAmountValueChange(it))
            },
            onDebounceValidation = { viewModel.onAmountUIEvent(OnAmountCompleted(it)) },
            isAmountError = viewModel.amountUIState.amountError.first,
            amountErrorMessage = stringResource(
                viewModel.amountUIState.amountError.second,
                viewModel.amountUIState.amountError.third
            ),
            currency = viewModel.amountUIState.currency,
            exchangeRate = viewModel.amountUIState.exchangeRateLabel.orEmpty(),
            convertedTotal = viewModel.amountUIState.convertedAmountLabel.orEmpty(),
            shouldDisplayExchange = viewModel.shouldDisplayExchange,
            onContinueClick = { viewModel.onAmountUIEvent(OnContinueClick) },
            enableButton = viewModel.amountUIState.enableButton,
            motive = viewModel.amountUIState.motive,
            onMotiveChange = { viewModel.onAmountUIEvent(OnMotiveChange(it)) }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MyContactsAmountBottomSheet(viewModel: MyContactsTransferAmountViewModel) {
    SmartPaymentBottomSheet(
        coroutineScope = rememberCoroutineScope(),
        modalBottomSheetState = viewModel.amountUIState.bottomSheetState,
        saveSendTitleResource = string.smart_payment_sheet_send_title,
        amount = viewModel.getFormattedAmount(),
        exchangedAmount = if (viewModel.shouldDisplayExchange) viewModel.amountUIState.convertedAmountLabel else null,
        fromIcon = viewModel.amountUIState.originAccountDisplay?.icon,
        fromTitle = stringResource(
            viewModel.amountUIState.originAccountDisplay?.sheetTitleResource ?: string.empty
        ),
        fromSubtitle = viewModel.amountUIState.originAccountDisplay?.sheetSubtitle,
        toContactInfo = {
            CustomInfoButton(
                title = viewModel.amountUIState.destinyAccountDisplay?.sheetTitle.orEmpty(),
                subtitle = viewModel.amountUIState.destinyAccountDisplay?.sheetSubtitle.orEmpty(),
                endIcon = null,
                startIcon = null,
                composableIcon = {
                    CustomContactIcon(
                        modifier = it,
                        name = viewModel.amountUIState.destinyAccountDisplay?.sheetTitle.orEmpty(),
                        color = MultimoneyTheme.colors.coloredInitialChar.random()
                    )
                },
                enable = false,
                titleIcon = if (viewModel.phoneAccount?.isFavorite == true) R.drawable.ic_star_filled else null
            )
        },
        motive = viewModel.amountUIState.motive,
        buttonText = stringResource(string.payment_amount_bottom_sheet_send_button),
        buttonAction = { viewModel.onAmountUIEvent(OnCallProcessTransfer) }
    )
}
