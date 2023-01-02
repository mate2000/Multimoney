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
import androidx.compose.runtime.collectAsState
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
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnAmountValueChange
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountViewModel.UIEvent.OnCallProcessTransfer
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
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.LoadingMultiMoney
import com.multimoney.multimoney.presentation.uielement.RoundedPaymentButton
import com.multimoney.multimoney.presentation.uielement.SmartPaymentBottomSheet
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.uielement.VoucherCurrencyExchangeInfo
import com.multimoney.multimoney.presentation.util.CARD_NUMBER_LAST_DIGITS
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SuggestionOrder
import com.multimoney.multimoney.presentation.util.filterInvalidAmountInput
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban
import com.multimoney.multimoney.presentation.util.transformation.CurrencyDoubleTransformation

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
        SmartPaymentSuccessScreen(viewModel)
        BackHandler {
            viewModel.onUIEvent(OnNavigateHome)
        }
    } else {
        SavingAmountContent(viewModel)
        SavingAmountBottomSheet(viewModel)
        BackHandler {
            viewModel.onUIEvent(OnNavigateBack)
        }
    }

    LoadingIndicator(viewModel.uiState.isLoading)
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
                    text = stringResource(id = R.string.smart_saving_amount_title),
                    style = Typography.h6.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MultimoneyTheme.colors.labelText
                    ),
                    textAlign = TextAlign.Left
                )
                CurrencyAmountInput(
                    modifier = Modifier.padding(top = 24.dp),
                    value = viewModel.uiState.currentAmountValueString.collectAsState().value,
                    placeHolder = stringResource(id = viewModel.uiState.placeholder),
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
                            .weight(0.32f),
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
                if (viewModel.shouldDisplayExchange) {
                    Spacer(modifier = Modifier.height(24.dp))
                    VoucherCurrencyExchangeInfo(
                        displayIcon = false,
                        mainRowAlignment = Arrangement.SpaceAround,
                        textColumnAlign = Alignment.CenterHorizontally,
                        leftTitleResource = R.string.payment_amount_bottom_sheet_exchange_type,
                        rightTitleResource = R.string.smart_saving_total_to_deposit,
                        exchangeRateText = viewModel.uiState.exchangeRateLabel,
                        convertedAmountText = viewModel.uiState.convertedAmountLabel
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SavingAmountBottomSheet(viewModel: SavingAmountViewModel) {
    SmartPaymentBottomSheet(
        coroutineScope = rememberCoroutineScope(),
        modalBottomSheetState = viewModel.uiState.bottomSheetState,
        saveSendTitleResource = R.string.smart_payment_amount_bottom_sheet_title,
        amount = viewModel.getFormattedAmount(),
        exchangedAmount = if (viewModel.shouldDisplayExchange) {
            viewModel.uiState.convertedAmountLabel
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
            viewModel.smartCurrency?.symbol ?: ""
        ),
        toSubtitle = if (viewModel.idBrand == Brand.CostaRica.id) {
            stringResource(viewModel.smartCurrency?.currencyName ?: R.string.empty)
        } else {
            null
        },
        toIcon = R.drawable.ic_multimoney_smart,
        buttonText = stringResource(R.string.button_continue),
        buttonAction = { viewModel.onUIEvent(OnCallProcessTransfer) }
    )
}
