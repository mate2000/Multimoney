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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnAmountCompleted
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnSuggestedAmountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnTryLater
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.RoundedPaymentButton
import com.multimoney.multimoney.presentation.uielement.SmartAmountBody
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SavingAmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SavingAmountViewModel = hiltViewModel()
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
        SmartPaymentSuccessScreen(
            viewModel = viewModel
        )
        BackHandler {
            viewModel.onUIEvent(OnNavigateHome)
        }
    } else {
        SavingAmountContent(viewModel)
        SmartPaymentConfirmBottomSheet(
            rememberCoroutineScope(),
            viewModel.uiState.bottomSheetState,
            viewModel
        )
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
fun SavingAmountContent(viewModel: SavingAmountViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        )
        SmartAmountBody(
            titleId = R.string.smart_saving_amount_title,
            currentAmount = viewModel.uiState.currentAmountValueString,
            amountPlaceHolderId = viewModel.uiState.placeholder,
            onAmountChange = {
                viewModel.onUIEvent(OnAmountValueChange(it))
            },
            onDebounceValidation = { viewModel.onUIEvent(OnAmountCompleted(it)) },
            currency = viewModel.uiState.currency,
            exchangeRate = viewModel.uiState.exchangeRateLabel,
            convertedTotal = viewModel.uiState.convertedAmountLabel,
            shouldDisplayExchange = viewModel.editAmountHelper.shouldDisplayExchange,
            onContinueClick = { viewModel.onUIEvent(OnContinueClick) },
            enableButton = viewModel.uiState.enableButton,
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
