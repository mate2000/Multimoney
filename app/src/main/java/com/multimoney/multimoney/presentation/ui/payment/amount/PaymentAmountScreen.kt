package com.multimoney.multimoney.presentation.ui.payment.amount

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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.multimoney.domain.model.balance.Summary
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.navgraph.CLIENT_BANK_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnMaximumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnMinimumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent.OnSaveArguments
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.RoundedPaymentButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation

@Composable
fun PaymentAmountScreen(
    navBackStackEntry: NavBackStackEntry,
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: PaymentAmountViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate)
            navBackStackEntry.arguments?.apply {
                viewModel.onUIEvent(
                    OnSaveArguments(
                        getString(USER),
                        getInt(ID_BRAND),
                        getInt(ID_CLIENT),
                        getInt(ID_LOAN_CLIENT),
                        (get(SUMMARY_LIST) as Array<Summary>).toList(),
                        getParcelable(CLIENT_BANK_ACCOUNT)
                    )
                )
            }
        }
    }

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
                onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) }
            )
            Text(
                modifier = Modifier.padding(top = 42.dp),
                text = stringResource(id = R.string.payment_amount_title),
                style = Typography.h5.copy(fontWeight = FontWeight.SemiBold, color = MultimoneyTheme.colors.labelText),
                textAlign = TextAlign.Left
            )
            Row(
                modifier = Modifier.padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomImage(
                    modifier = Modifier.padding(top = 3.dp),
                    drawableResource = R.drawable.info_blue_icon
                )
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.payment_amount_subtitle))
                        append(" ")
                        withStyle(
                            style = Typography.subtitle1.toSpanStyle()
                                .copy(
                                    fontWeight = FontWeight.Bold
                                )
                        ) {
                            append(viewModel.uiState.minimumPaymentLabel)
                        }
                    },
                    style = Typography.subtitle1,
                    color = MultimoneyTheme.colors.labelText,
                    textAlign = TextAlign.Left
                )
            }
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
                    isRequiredMessage = stringResource(
                        id = R.string.payment_amount_amount_min_error,
                        viewModel.getFormattedCurrency()
                    ),
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
