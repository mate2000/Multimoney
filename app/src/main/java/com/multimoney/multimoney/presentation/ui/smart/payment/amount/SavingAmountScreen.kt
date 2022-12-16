package com.multimoney.multimoney.presentation.ui.smart.payment.amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.Companion.SAVING_PLACEHOLDER
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnNavigateHome
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnRetryTransfer
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnSuggestedAmountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnTryLater
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.RoundedPaymentButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder
import com.multimoney.multimoney.presentation.util.filterInvalidAmountInput
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation

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
        LoadingMultiMoney(string.smart_processing_transaction)
    } else if (viewModel.uiState.showErrorScreen) {
        val notificationTitle = stringResource(string.smart_saving_try_later_notification_title)
        val notificationBody = stringResource(string.smart_saving_try_later_notification_body)
        AlertResult(
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateHome) },
            titleResource = string.error_occurred_title,
            descriptionResource = string.error_please_try_again,
            buttonTextResource = string.error_button_retry,
            onButtonClick = { viewModel.onUIEvent(OnRetryTransfer) },
            isSecondaryButtonVisible = true,
            secondaryButtonTextResource = string.error_button_try_later,
            onSecondaryButtonClick = {
                viewModel.onUIEvent(
                    OnTryLater(
                        notificationTitle,
                        notificationBody,
                        drawable.ic_logo_multimoney,
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
}

@Composable
@Preview
fun SavingAmountContent(viewModel: SavingAmountViewModel = hiltViewModel()) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MultimoneyTheme.colors.background)
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    modifier = Modifier.padding(top = 30.dp),
                    text = stringResource(id = string.smart_saving_amount_title),
                    style = Typography.h6.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MultimoneyTheme.colors.labelText
                    ),
                    textAlign = TextAlign.Left
                )
                CurrencyAmountInput(
                    modifier = Modifier.padding(top = 24.dp),
                    value = viewModel.uiState.currentAmountValueString,
                    placeHolder = SAVING_PLACEHOLDER,
                    onValueChange = {
                        viewModel.onUIEvent(OnAmountValueChange(it.filterInvalidAmountInput()))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    isRequired = true,
                    customTransformation = CurrencyDoubleTransformation(
                        viewModel.uiState.currency,
                        CreditAmountViewModel.CURRENCY_SEPARATOR
                    )
                )
                Row(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                ) {
                    RoundedPaymentButton(
                        modifier = Modifier
                            .weight(0.30f),
                        onClick = { viewModel.onUIEvent(OnSuggestedAmountClick(viewModel.uiState.minSuggestion)) },
                        strokeWidth = 1.dp,
                        roundedShapeDp = 24.dp,
                        mainText = viewModel.uiState.minSuggestion.display,
                        isSelected = viewModel.verifySuggestionSelected(SuggestionOrder.MIN),
                        textAlign = Alignment.CenterHorizontally
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                    RoundedPaymentButton(
                        modifier = Modifier
                            .weight(0.30f),
                        onClick = { viewModel.onUIEvent(OnSuggestedAmountClick(viewModel.uiState.mediumSuggestion)) },
                        strokeWidth = 1.dp,
                        roundedShapeDp = 24.dp,
                        mainText = viewModel.uiState.mediumSuggestion.display,
                        isSelected = viewModel.verifySuggestionSelected(SuggestionOrder.MEDIUM),
                        textAlign = Alignment.CenterHorizontally
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                    RoundedPaymentButton(
                        modifier = Modifier
                            .weight(0.30f),
                        onClick = { viewModel.onUIEvent(OnSuggestedAmountClick(viewModel.uiState.maxSuggestion)) },
                        strokeWidth = 1.dp,
                        roundedShapeDp = 24.dp,
                        mainText = viewModel.uiState.maxSuggestion.display,
                        isSelected = viewModel.verifySuggestionSelected(SuggestionOrder.MAX),
                        textAlign = Alignment.CenterHorizontally
                    )
                }
            }
            CustomButton(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onUIEvent(OnContinueClick)
                },
                text = stringResource(id = string.button_continue),
                buttonType = CustomButtonType.PrimaryPrimary,
                enable = viewModel.uiState.enableButton
            )
        }
    }
}
