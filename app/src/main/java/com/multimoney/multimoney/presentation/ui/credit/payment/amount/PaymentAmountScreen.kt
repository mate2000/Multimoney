package com.multimoney.multimoney.presentation.ui.credit.payment.amount

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
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnMaximumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountViewModel.UIEvent.OnMinimumPaymentButtonClick
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.RoundedPaymentButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation

@Composable
fun PaymentAmountScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentAmountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack)
    }
    PaymentAmountContent(viewModel)
}

@Composable
@Preview
fun PaymentAmountContent(
    viewModel: PaymentAmountViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) },
                onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBackHome) }
            )
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
            },
            text = stringResource(id = R.string.button_continue),
            buttonType = CustomButtonType.PrimaryPrimary,
            enable = viewModel.uiState.enableButton
        )
    }
}
