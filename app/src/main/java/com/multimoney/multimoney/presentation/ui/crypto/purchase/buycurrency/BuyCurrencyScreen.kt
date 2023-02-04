package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.PurchaseConfirmationBottomSheet
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CryptoCurrencyInputLayout
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.VoucherCurrencyExchangeInfo
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.roundToEightDecimalPlaces
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

@Composable
fun BuyCurrencyScreen(
    sharedViewModel: PurchaseCryptoSharedViewModel = hiltViewModel(),
    viewModel: BuyCurrencyScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(
            BuyCurrencyScreenViewModel.UIEvent.OnSetUserData(
                idCurrencyAccount = sharedViewModel.uiState.idCurrency,
                pkUser = sharedViewModel.pkUser.toInt(),
                asset = sharedViewModel.uiState.asset ?: "",
                cryptoNetwork = sharedViewModel.uiState.cryptoNetWork,
                idBrand = sharedViewModel.idBrand,
                user = sharedViewModel.user,
                market = sharedViewModel.uiState.market,
                identification = sharedViewModel.identification,
                accountToken = sharedViewModel.uiState.accounts.firstOrNull()?.accountToken?.toLong() ?: 0L,
                side = sharedViewModel.side,
                assetImageUrl = sharedViewModel.uiState.assetImageBaseUrl,
                smartAccountAvailableBalance = sharedViewModel.uiState.smartAccountAvailableBalance
            )
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(BuyCurrencyScreenViewModel.UIEvent.OnGetQuoteAndCommissions)
    }

    BuyCurrencyScreenContent(viewModel)
    BackHandler {
        viewModel.isTimerRunning = false
        sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BuyCurrencyScreenContent(
    viewModel: BuyCurrencyScreenViewModel
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            PurchaseConfirmationBottomSheet()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MultimoneyTheme.colors.background)
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxSize()
            ) {
                val (title, conversionCurrencyToDollars, exchangeRate, amountInput, counter, button) = createRefs()
                TitleSection(
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(conversionCurrencyToDollars.top)
                    },
                    imageUrl = viewModel.assetImageUrl,
                    currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
                        ?: DEFAULT_CURRENCY_PRICE,
                    asset = viewModel.asset
                )
                AmountInputSection(
                    modifier = Modifier.constrainAs(amountInput) {
                        top.linkTo(title.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    asset = viewModel.asset,
                    currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
                        ?: DEFAULT_CURRENCY_PRICE,
                    quoteAmount = viewModel.uiState.quoteAmount,
                    baseAmount = viewModel.uiState.baseAmount,
                    isError = false, // change for input validation returned value
                    errorText = viewModel.uiState.error,
                )
                if (viewModel.idCurrencyAccount == CurrencyType.Colon.id) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .constrainAs(exchangeRate) {
                                top.linkTo(amountInput.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .padding(vertical = 16.dp)
                    ) {
                        VoucherCurrencyExchangeInfo(
                            leftTitleResource = R.string.crypto_purchase_flow_exchange_type_title,
                            rightTitleResource = R.string.crypto_purchase_flow_exchange_total_title,
                            exchangeRateText = viewModel.uiState.exchangeRate.toCurrencyFormat(symbol = CurrencyType.Colon.symbol),
                            convertedAmountText = (viewModel.uiState.quoteAmount.value.ifEmpty { EMPTY_CURRENCY }.toDouble()
                                    * viewModel.uiState.exchangeRate).toCurrencyFormat(symbol = CurrencyType.Colon.symbol),
                            textColumnAlign = Alignment.CenterHorizontally,
                            displayIcon = false
                        )
                    }
                }
                CounterSection(
                    modifier = Modifier.constrainAs(counter) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(button.top)
                    },
                    smartAccountAvailableBalance = viewModel.smartAccountAvailableBalance,
                    downCounter = viewModel.timerCount ?: DEFAULT_TIMER_REMAINING_SECS,
                )
                CustomButton(
                    modifier = Modifier
                        .constrainAs(button) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    enable = false, // change for input validation returned value
                )
            }
        }
    }
}

@Composable
fun CounterSection(
    modifier: Modifier = Modifier,
    downCounter: Int,
    smartAccountAvailableBalance: Double,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                        )
                    ) {
                        append(stringResource(id = R.string.crypto_purchase_flow_available_smart_amount))
                    }
                    append(SPACE_BETWEEN)
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(smartAccountAvailableBalance.toCurrencyFormat())
                    }
                },
                style = Typography.body2
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                        )
                    ) {
                        append(stringResource(id = R.string.crypto_purchase_flow_price_expires_in))
                    }
                    append(SPACE_BETWEEN)
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(downCounter.toString())
                        append(TIMER_UNIT_INDICATOR)
                    }
                },
                style = Typography.body2
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AmountInputSection(
    modifier: Modifier = Modifier,
    asset: String,
    currencyPrice: Double,
    isError: Boolean = false,
    errorText: ErrorTextHelper = ErrorTextHelper.ErrorText(""),
    quoteAmount: MutableState<String>,
    baseAmount: MutableState<String>
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val quoteAmountText = remember { quoteAmount }
    val baseAmountText = remember { baseAmount }
    val isTransformationCurrency = remember { mutableStateOf(false) }
    val baseOrQuote = if (isTransformationCurrency.value.not()) quoteAmountText else baseAmountText

    LaunchedEffect(key1 = true) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CryptoCurrencyInputLayout(
            value = baseOrQuote,
            iconCurrency = asset,
            focusRequester = focusRequester,
            isError = isError,
            errorText = errorText.getErrorText(context),
            isTransformationCurrency = isTransformationCurrency,
            onImeClick = { keyboardController?.hide() }
        )
        Text(
            text = if (isTransformationCurrency.value.not()) {
                stringResource(
                    id = R.string.crypto_purchase_flow_exchange_reference_edittext,
                    (quoteAmountText.value.ifEmpty { EMPTY_CURRENCY }
                        .toDouble() / currencyPrice).roundToEightDecimalPlaces(),
                    asset
                )
            } else {
                stringResource(
                    id = R.string.crypto_purchase_flow_exchange_reference_edittext_dollars,
                    (currencyPrice * baseAmountText.value.ifEmpty { EMPTY_CURRENCY }
                        .toDouble()).toCurrencyFormat()
                )
            },
            style = Typography.body2.copy(
                color = MultimoneyTheme.colors.bodyTextColor
            )
        )
    }
}

@Composable
fun TitleSection(
    modifier: Modifier = Modifier,
    imageUrl: String,
    asset: String,
    currencyPrice: Double
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(id = R.string.crypto_purchase_flow_title),
                style = Typography.h6.copy(
                    color = MultimoneyTheme.colors.text,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(
                    id = R.string.crypto_purchase_flow_exchange_reference,
                    asset,
                    currencyPrice.toCurrencyFormat()
                ),
                style = Typography.subtitle1.copy(
                    color = MultimoneyTheme.colors.text
                )
            )
        }
    }
}

const val DEFAULT_CURRENCY_PRICE = 0.0
const val DEFAULT_TIMER_REMAINING_SECS = 15
const val TIMER_UNIT_INDICATOR = " seg"
const val SPACE_BETWEEN = " "
const val EMPTY_CURRENCY = "0.00"
