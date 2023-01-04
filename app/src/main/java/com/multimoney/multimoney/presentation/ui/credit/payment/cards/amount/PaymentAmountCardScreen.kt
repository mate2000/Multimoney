package com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAlertResultButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAlertResultRightButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnHidePaymentBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnMinimumPaymentButtonClick
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CurrencyAmountInput
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.RoundedPaymentButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.uielement.VisaAnimation
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PaymentAmountCardScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: PaymentAmountCardViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    viewModel.onUIEvent(OnStart(stringResource(id = R.string.error_no_internet_title)))

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
        }
    }
    PaymentAmountCardContent(viewModel, coroutineScope)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun PaymentAmountCardContent(
    viewModel: PaymentAmountCardViewModel = hiltViewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val focusManager = LocalFocusManager.current
    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            titleString = viewModel.uiState.alertResultTitle,
            descriptionString = viewModel.uiState.alertResultDescription,
            buttonTextResource = string.payment_amount_error_button,
            isLeftButtonVisible = false,
            isRightButtonVisible = true,
            onRightButtonClick = { viewModel.onUIEvent(OnAlertResultRightButtonClick) },
            onButtonClick = { viewModel.onUIEvent(OnAlertResultButtonClick) }
        )
    } else {
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
                        modifier = Modifier.padding(top = 34.dp),
                        text = stringResource(id = viewModel.uiState.titleResource),
                        style = Typography.h5.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MultimoneyTheme.colors.labelText
                        ),
                        textAlign = TextAlign.Left
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RoundedPaymentButton(
                            modifier = Modifier.wrapContentSize(),
                            onClick = { viewModel.onUIEvent(OnMinimumPaymentButtonClick) },
                            strokeWidth = 0.dp,
                            roundedShapeDp = 30.dp,
                            mainText = viewModel.uiState.minimumPaymentLabel,
                            secondaryText = stringResource(id = string.payment_amount_card_min_amount),
                            isSelected = false,
                            isSingleLine = true
                        )
                    }
                    CurrencyAmountInput(
                        modifier = Modifier.padding(top = 16.dp),
                        value = viewModel.uiState.currentAmountValueString,
                        placeHolder = viewModel.uiState.currentAmountValueString,
                        onValueChange = {
                            viewModel.onUIEvent(OnAmountValueChange(it))
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
                CustomButton(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.onUIEvent(UIEvent.OnContinueClick)
                    },
                    text = stringResource(id = R.string.button_continue),
                    buttonType = CustomButtonType.PrimaryPrimary,
                    enable = viewModel.uiState.enableButton
                )
            }
        }
    }

    if (viewModel.uiState.isVisaAnimationVisible) {
        VisaAnimation { viewModel.onUIEvent(UIEvent.OnFinishVisaAnimation) }
    }
    PaymentAmountCardBottomSheetScreen(
        viewModel,
        coroutineScope,
        viewModel.uiState.bottomSheetVisibleState
    )
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
    LoadingIndicator(viewModel.uiState.isLoading)
}
