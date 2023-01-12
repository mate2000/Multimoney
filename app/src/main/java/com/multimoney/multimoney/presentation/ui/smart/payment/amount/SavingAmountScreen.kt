package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnCallProcessTransfer
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.common.editamount.BaseSmartEditAmountViewModel.BaseUIEvent.OnTryLater
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnSuggestedAmountClick
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.RoundedPaymentButton
import com.multimoney.multimoney.presentation.uielement.SmartAmountBody
import com.multimoney.multimoney.presentation.uielement.SmartPaymentBottomSheet
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.CARD_NUMBER_LAST_DIGITS
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun SavingAmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SavingAmountViewModel = hiltViewModel()
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
        SmartPaymentSuccessScreen(viewModel)
        BackHandler {
            viewModel.onBaseUIEvent(OnNavigateHome)
        }
    } else {
        SavingAmountContent(viewModel)
        SavingAmountBottomSheet(viewModel)
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
fun SavingAmountContent(viewModel: SavingAmountViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { viewModel.onBaseUIEvent(OnNavigateBack) }
        )
        SmartAmountBody(
            titleId = R.string.smart_saving_amount_title,
            currentAmount = viewModel.baseUIState.currentAmountValueString,
            amountPlaceHolderId = viewModel.baseUIState.placeholder,
            onAmountChange = {
                viewModel.onBaseUIEvent(OnAmountValueChange(it))
            },
            onDebounceValidation = { viewModel.onBaseUIEvent(OnAmountCompleted(it)) },
            currency = viewModel.baseUIState.currency,
            exchangeRate = viewModel.baseUIState.exchangeRateLabel,
            convertedTotal = viewModel.baseUIState.convertedAmountLabel,
            shouldDisplayExchange = viewModel.shouldDisplayExchange,
            onContinueClick = { viewModel.onBaseUIEvent(OnContinueClick) },
            enableButton = viewModel.baseUIState.enableButton,
            suggestions = { QuantitySuggestions(viewModel) }
        )
    }
}

@Composable
fun QuantitySuggestions(viewModel: SavingAmountViewModel = hiltViewModel()) {
    Row(
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
    ) {
        RoundedPaymentButton(
            modifier = Modifier.weight(0.32f),
            onClick = { viewModel.onUIEvent(OnSuggestedAmountClick(viewModel.uiState.minSuggestion)) },
            strokeWidth = 1.dp,
            roundedShapeDp = 24.dp,
            mainText = viewModel.uiState.minSuggestion.display,
            isSelected = viewModel.verifySuggestionSelected(SuggestionOrder.MIN),
            textAlign = Alignment.CenterHorizontally
        )
        Spacer(modifier = Modifier.weight(0.02f))
        RoundedPaymentButton(
            modifier = Modifier.weight(0.32f),
            onClick = { viewModel.onUIEvent(OnSuggestedAmountClick(viewModel.uiState.mediumSuggestion)) },
            strokeWidth = 1.dp,
            roundedShapeDp = 24.dp,
            mainText = viewModel.uiState.mediumSuggestion.display,
            isSelected = viewModel.verifySuggestionSelected(SuggestionOrder.MEDIUM),
            textAlign = Alignment.CenterHorizontally
        )
        Spacer(modifier = Modifier.weight(0.02f))
        RoundedPaymentButton(
            modifier = Modifier.weight(0.32f),
            onClick = { viewModel.onUIEvent(OnSuggestedAmountClick(viewModel.uiState.maxSuggestion)) },
            strokeWidth = 1.dp,
            roundedShapeDp = 24.dp,
            mainText = viewModel.uiState.maxSuggestion.display,
            isSelected = viewModel.verifySuggestionSelected(SuggestionOrder.MAX),
            textAlign = Alignment.CenterHorizontally
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SavingAmountBottomSheet(viewModel: SavingAmountViewModel) {
    SmartPaymentBottomSheet(
        coroutineScope = rememberCoroutineScope(),
        modalBottomSheetState = viewModel.baseUIState.bottomSheetState,
        saveSendTitleResource = R.string.smart_payment_amount_bottom_sheet_title,
        amount = viewModel.getFormattedAmount(),
        exchangedAmount = if (viewModel.shouldDisplayExchange) {
            viewModel.baseUIState.convertedAmountLabel
        } else {
            null
        },
        fromLabel = stringResource(viewModel.sheetSubtitle),
        fromTitle = viewModel.bankDetail,
        fromSubtitle = if (viewModel.idBrand == Brand.CostaRica.id) {
            getMaskedAccountIban(
                viewModel.ibanAccount?.sinpeAccount ?: "",
                stringResource(R.string.payment_account_masked_text)
            )
        } else {
            stringResource(
                R.string.visa_card_masked_number,
                viewModel.maskedCardNumber.takeLast(CARD_NUMBER_LAST_DIGITS)
            )
        },
        fromIcon = viewModel.originIcon,
        toLabel = stringResource(R.string.smart_payment_amount_bottom_sheet_to),
        toTitle = stringResource(
            R.string.smart_payment_amount_bottom_sheet_my_smart_account,
            viewModel.ibanCurrency?.symbol ?: ""
        ),
        toSubtitle = if (viewModel.idBrand == Brand.CostaRica.id) {
            stringResource(viewModel.smartCurrency?.currencyName ?: R.string.empty)
        } else {
            null
        },
        toIcon = R.drawable.ic_multimoney_smart,
        buttonText = stringResource(R.string.button_continue),
        buttonAction = { viewModel.onBaseUIEvent(OnCallProcessTransfer) }
    )
}
