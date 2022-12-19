package com.multimoney.multimoney.presentation.ui.smart.payment.amount

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
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
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModelCopy.Companion.SAVING_PLACEHOLDER
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModelCopy.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModelCopy.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModelCopy.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModelCopy.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModelCopy.UIEvent.OnSuggestedAmountClick
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.RoundedPaymentButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.uielement.VoucherCurrencyExchangeInfo
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder
import com.multimoney.multimoney.presentation.util.filterInvalidAmountInput
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation

@Composable
fun SavingAmountScreen1(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SavingAmountViewModelCopy = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack)
            onUIEvent(OnStart)
        }
    }

    SavingAmountContent(viewModel)

    LoadingIndicator(viewModel.uiState.isLoading)

    // Todo Add Saving Bottom Sheet. ViewModel already handles the bottomSheetVisibleState on continue click
}

@Composable
@Preview
fun SavingAmountContent(viewModel: SavingAmountViewModelCopy = hiltViewModel()) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier.background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            isRightButtonVisible = false,
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
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
                    text = stringResource(id = R.string.smart_saving_amount_title),
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
                        viewModel.smartCurrency?.symbol ?: CurrencyType.Dollar.symbol,
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
                if (viewModel.shouldDisplayExchange) {
                    VoucherCurrencyExchangeInfo(
                        leftTitleResource = R.string.payment_amount_bottom_sheet_exchange_type,
                        rightTitleResource = R.string.payment_amount_bottom_sheet_amount_to_debit,
                        exchangeRateText = viewModel.uiState.exchangeRate.toString(),
                        convertedAmountText = viewModel.uiState.exchangeConvertedAmount.toString()
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
                text = stringResource(id = R.string.button_continue),
                buttonType = CustomButtonType.PrimaryPrimary,
                enable = viewModel.uiState.enableButton
            )
        }
    }
}

@Composable
fun ExchangeRateRow(modifier: Modifier, rate: Double) {
    Row(modifier = modifier.fillMaxWidth()) {
        Column() {
            
        }
        Column() {
            
        }
    }
}