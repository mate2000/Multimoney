package com.multimoney.multimoney.presentation.ui.credit.payment.amount

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.creditamount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnAlertResultButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnCallQueryGetExchangeRateCredit
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnHidePaymentBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnMaximumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnMinimumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.RoundedPaymentButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PaymentAmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: PaymentAmountViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
            if (shouldDisplayExchangeRate()) {
                onUIEvent(OnCallQueryGetExchangeRateCredit)
            }
        }
    }
    BackHandler {
        when {
            viewModel.uiState.bottomSheetVisibleState.isVisible -> {
                coroutineScope.launch {
                    viewModel.onUIEvent(OnHidePaymentBottomSheet)
                }
            }
            else -> viewModel.onUIEvent(OnNavigateBack)
        }
    }
    PaymentAmountContent(viewModel, coroutineScope)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun PaymentAmountContent(
    viewModel: PaymentAmountViewModel = hiltViewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            titleString = viewModel.uiState.alertResultTitle,
            descriptionString = viewModel.uiState.alertResultDescription,
            buttonTextResource = R.string.payment_amount_error_button,
            isLeftButtonVisible = false,
            isRightButtonVisible = false,
            onButtonClick = { viewModel.onUIEvent(OnAlertResultButtonClick) }
        )
    } else {
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier.background(MultimoneyTheme.colors.background)
        ) {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) },
                onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBackHome) }
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
                        modifier = Modifier.padding(top = 42.dp),
                        text = stringResource(id = R.string.payment_amount_title),
                        style = Typography.h5.copy(fontWeight = FontWeight.SemiBold, color = MultimoneyTheme.colors.labelText),
                        textAlign = TextAlign.Left
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth()
                    ) {
                        RoundedPaymentButton(
                            modifier = Modifier
                                .weight(0.48f),
                            onClick = { viewModel.onUIEvent(OnMinimumPaymentButtonClick) },
                            strokeWidth = 1.dp,
                            roundedShapeDp = 24.dp,
                            mainText = viewModel.uiState.minimumPaymentLabel,
                            secondaryText = stringResource(id = R.string.payment_amount_min_amount),
                            isSelected = viewModel.uiState.isMinimumSelected
                        )
                        Spacer(modifier = Modifier.weight(0.04f))
                        RoundedPaymentButton(
                            modifier = Modifier
                                .weight(0.48f),
                            onClick = { viewModel.onUIEvent(OnMaximumPaymentButtonClick) },
                            strokeWidth = 1.dp,
                            roundedShapeDp = 24.dp,
                            mainText = viewModel.uiState.maximumPaymentLabel,
                            secondaryText = stringResource(id = R.string.payment_amount_max_amount),
                            isSelected = viewModel.uiState.isMaximumSelected
                        )
                    }
                    if (viewModel.uiState.isAmountVisible) {
                        CurrencyAmountInput(
                            modifier = Modifier.padding(top = 24.dp),
                            value = viewModel.uiState.currentAmountValueString,
                            placeHolder = viewModel.uiState.currentAmountValueString,
                            onValueChange = {
                                viewModel.onUIEvent(UIEvent.OnAmountValueChange(it))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                            }),
                            isRequired = true,
                            isError = viewModel.uiState.currentAmountError.first,
                            errorMessage = stringResource(
                                id = viewModel.uiState.currentAmountError.second,
                                viewModel.getFormattedCurrency()
                            ),
                            customTransformation = CurrencyDoubleTransformation(
                                viewModel.uiState.currency,
                                CreditAmountViewModel.CURRENCY_SEPARATOR
                            )
                        )
                    }
                }
                CustomButton(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onUIEvent(UIEvent.OnPaymentButtonClick)
                    },
                    text = stringResource(id = R.string.button_continue),
                    buttonType = CustomButtonType.PrimaryPrimary,
                    enable = viewModel.uiState.enableButton
                )
            }
        }
    }
    PaymentAmountBottomSheetScreen(
        viewModel,
        coroutineScope,
        viewModel.uiState.bottomSheetVisibleState,
        stringResource(id = R.string.payment_amount_bottom_sheet_process_payment_description_label)
    )
    LoadingIndicator(viewModel.uiState.isLoading)
}
