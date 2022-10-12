package com.multimoney.multimoney.presentation.ui.payment.amount

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.ComplementaryBlack
import com.multimoney.multimoney.presentation.theme.ComplementaryGray
import com.multimoney.multimoney.presentation.theme.GradientGrey1
import com.multimoney.multimoney.presentation.theme.GradientGrey2
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountViewModel.UIEvent
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.RoundedPaymentButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.transformation.CurrencyIntegerTransformation

@Composable
fun HowMuchYouWantPayScreen(
    navBackStackEntry: NavBackStackEntry,
    onPopBackStack: () -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: PaymentAmountViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Column {
            TopNavBar(
                onLeftButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) },
                onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateBack) }
            )
            Text(
                modifier = Modifier.padding(top = 42.dp),
                text = stringResource(id = R.string.payment_amount_title),
                style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )
            Row(
                modifier = Modifier.padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomImage(
                    drawableResource = R.drawable.info_blue_icon
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 10.dp),
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
        }
        Row(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        ) {
            RoundedPaymentButton(
                modifier = Modifier
                    .weight(0.48f),
                onClick = { viewModel.setCurrentValueToMin() },
                strokeBrush = Brush.verticalGradient(
                    colors = getBrushColorForRoundedButtons(viewModel, true)
                ),
                strokeWidth = 1.dp,
                roundedShapeDp = 24.dp,
                backgroundColor = if (isSystemInDarkTheme()) {
                    ComplementaryBlack
                } else {
                    ComplementaryBlack
                },
                mainText = viewModel.uiState.minimumPaymentLabel,
                secondaryText = stringResource(id = R.string.payment_amount_min_amount),
                mainTextColor = MultimoneyTheme.colors.textLink,
                secondaryTextColor = if (isSystemInDarkTheme()) {
                    ComplementaryGray
                } else {
                    ComplementaryGray
                }
            )
            Spacer(modifier = Modifier.weight(0.04f))
            RoundedPaymentButton(
                modifier = Modifier
                    .weight(0.48f),
                onClick = { viewModel.setCurrentValueToMax() },
                strokeBrush = Brush.verticalGradient(
                    colors = getBrushColorForRoundedButtons(viewModel, false)
                ),
                strokeWidth = 1.dp,
                roundedShapeDp = 24.dp,
                backgroundColor = if (isSystemInDarkTheme()) {
                    ComplementaryBlack
                } else {
                    ComplementaryBlack
                },
                mainText = viewModel.uiState.maximumPaymentLabel,
                secondaryText = stringResource(id = R.string.payment_amount_max_amount),
                mainTextColor = MultimoneyTheme.colors.textLink,
                secondaryTextColor = if (isSystemInDarkTheme()) {
                    ComplementaryGray
                } else {
                    ComplementaryGray
                }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        CurrencyAmountInput(
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
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            isRequired = false,
            isError = viewModel.uiState.currentAmountError.first,
            errorMessage = stringResource(
                id = viewModel.uiState.currentAmountError.second,
                viewModel.getFormattedCurrency()
            ),
            customTransformation = CurrencyIntegerTransformation(
                viewModel.uiState.currency,
                CreditAmountViewModel.CURRENCY_SEPARATOR
            ),
            onDebounceValidation = {
                viewModel.onUIEvent(UIEvent.OnAmountValueChangeFinished(it))
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Center
        ) {
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
}

@Composable
fun getBrushColorForRoundedButtons(
    viewModel: PaymentAmountViewModel,
    isMinButton: Boolean
): List<Color> {
    val unselectedColors = if (isSystemInDarkTheme()) {
        listOf(
            GradientGrey1,
            GradientGrey2
        )
    } else {
        listOf(
            GradientGrey1,
            GradientGrey2
        )
    }
    val selectedColors = if (isSystemInDarkTheme()) {
        listOf(
            MultimoneyTheme.colors.primary,
            MultimoneyTheme.colors.primary
        )
    } else {
        listOf(
            MultimoneyTheme.colors.primary,
            MultimoneyTheme.colors.primary
        )
    }
    return if (isMinButton) {
        if (viewModel.uiState.currentAmountValue == viewModel.minAmountValue) {
            selectedColors
        } else {
            unselectedColors
        }
    } else {
        if (viewModel.uiState.currentAmountValue == viewModel.maxAmountValue) {
            selectedColors
        } else {
            unselectedColors
        }
    }
}
