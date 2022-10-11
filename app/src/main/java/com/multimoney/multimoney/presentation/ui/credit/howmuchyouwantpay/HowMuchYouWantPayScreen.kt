package com.multimoney.multimoney.presentation.ui.credit.howmuchyouwantpay

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
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
import com.multimoney.multimoney.presentation.theme.*
import com.multimoney.multimoney.presentation.ui.credit.creditamount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.credit.howmuchyouwantpay.HowMuchYouWantPayViewModel.UIEvent
import com.multimoney.multimoney.presentation.uielement.*
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.stringToIntegerFormat
import com.multimoney.multimoney.presentation.util.transformation.CurrencyIntegerTransformation
import com.multimoney.multimoney.util.Currency

@Composable
fun HowMuchYouWantPayScreen(
    navBackStackEntry: NavBackStackEntry,
    onPopBackStack: () -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: HowMuchYouWantPayViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val errorMessage = stringResource(id = viewModel.uiState.currentAmountError.second) + " " +
        if (viewModel.uiState.currentAmountError.second == R.string.how_much_you_want_to_pay_amount_max_error) {
            viewModel.uiState.maxAmountValue.toString().stringToIntegerFormat()
        } else {
            viewModel.uiState.minAmountValue.toString().stringToIntegerFormat()
        }
    LaunchedEffect(true) {
        viewModel.apply {
            onUIEvent(
                UIEvent.OnGetTextResources(
                    "5"
                    /*navBackStackEntry.arguments?.getString(
                        ID_BRAND,
                        ""
                    ) ?: ""*/
                )
            )
        }
    }
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
                modifier = Modifier.padding(top = 40.dp),
                text = stringResource(id = viewModel.uiState.titleResource),
                style = Typography.h5.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    modifier = Modifier.padding(top = 20.dp),
                    painter = painterResource(id = R.drawable.info_blue_icon),
                    contentDescription = "Info icon"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = buildAnnotatedString {
                        append(stringResource(id = viewModel.uiState.subtitleResource))
                        append(" ")
                        withStyle(
                            style = Typography.subtitle1.toSpanStyle()
                                .copy(
                                    fontWeight = FontWeight.Bold
                                )
                        ) {
                            append(
                                stringResource(
                                    id = viewModel.uiState.minAmountResource,
                                    viewModel.uiState.minAmountValue.toString().stringToIntegerFormat()
                                )
                            )
                        }
                    },
                    style = Typography.subtitle1,
                    color = MultimoneyTheme.colors.labelText,
                    textAlign = TextAlign.Left
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MultimoneyTheme.colors.background)
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
                    mainText = stringResource(
                        id = viewModel.uiState.minAmountResource,
                        viewModel.uiState.minAmountValue.toString().stringToIntegerFormat()
                    ),
                    secondaryText = stringResource(id = viewModel.uiState.subtitleMinButton),
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
                    mainText = stringResource(
                        id = viewModel.uiState.maxAmountResource,
                        viewModel.uiState.maxAmountValue.toString().stringToIntegerFormat()
                    ),
                    secondaryText = stringResource(id = viewModel.uiState.subtitleMaxButton),
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
                value = buildAnnotatedString {
                    withStyle(
                        style = Typography.subtitle1.toSpanStyle()
                            .copy(
                                fontWeight = FontWeight.Bold
                            )
                    ) {
                        append(viewModel.uiState.currentAmountValueString)
                    }
                }.toString(),
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
                errorMessage = errorMessage,
                customTransformation = CurrencyIntegerTransformation(
                    Currency.COSTA_RICA,
                    CreditAmountViewModel.CURRENCY_SEPARATOR
                ),
                onDebounceValidation = {
                    viewModel.onUIEvent(UIEvent.OnValidateAmount)
                }
            )
        }
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
    viewModel: HowMuchYouWantPayViewModel,
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
        if (viewModel.uiState.currentAmountValue == viewModel.uiState.minAmountValue) {
            selectedColors
        } else {
            unselectedColors
        }
    } else {
        if (viewModel.uiState.currentAmountValue == viewModel.uiState.maxAmountValue) {
            selectedColors
        } else {
            unselectedColors
        }
    }
}
