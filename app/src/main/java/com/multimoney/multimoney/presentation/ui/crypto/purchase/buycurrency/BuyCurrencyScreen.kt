package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
                asset = sharedViewModel.uiState.asset ?: "",
                cryptoNetwork = sharedViewModel.uiState.cryptoNetWork,
                idBrand = sharedViewModel.idBrand,
                user = sharedViewModel.user,
                market = sharedViewModel.uiState.market,
                identification = sharedViewModel.identification,
                side = sharedViewModel.side,
                assetImageUrl = sharedViewModel.uiState.assetImageBaseUrl,
                smartAccountAvailableBalance = sharedViewModel.uiState.smartAccountAvailableBalance,
            )
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(BuyCurrencyScreenViewModel.UIEvent.OnGetExchangeRate)
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
                val (title, conversionCurrencyToDollars, amountInput, counter, button) = createRefs()
                TitleSection(
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(conversionCurrencyToDollars.top)
                    },
                    imageUrl = viewModel.assetImageUrl,
                    currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price ?: 0.0,
                    asset = viewModel.asset
                )
                AmountInputSection(
                    modifier = Modifier.constrainAs(amountInput) {
                        top.linkTo(title.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    asset = viewModel.asset,
                    currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price ?: 0.0,
                    quoteAmount = viewModel.uiState.quoteAmount,
                    baseAmount = viewModel.uiState.baseAmount,
                )
                CounterSection(
                    modifier = Modifier.constrainAs(counter) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(button.top)
                    },
                    smartAccountAvailableBalance = viewModel.smartAccountAvailableBalance,
                    downCounter = viewModel.timerCount ?: 15
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
                    enable = viewModel.uiState.quoteAmount.value.isNotEmpty()
                            || viewModel.uiState.baseAmount.value.isNotEmpty(),
                )
            }
        }
    }
}

@Composable
fun CounterSection(
    modifier: Modifier = Modifier,
    downCounter: Int,
    smartAccountAvailableBalance: Double
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
                    append(" ")
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(smartAccountAvailableBalance.toCurrencyFormat())
                    }
                }
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
                    append(" ")
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(downCounter.toString())
                        append(" seg")
                    }
                }
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
    quoteAmount: MutableState<String>,
    baseAmount: MutableState<String>
) {

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
            isTransformationCurrency = isTransformationCurrency,
            onImeClick = {
                keyboardController?.hide()
            }
        )
        Text(
            text = if (isTransformationCurrency.value.not()) {
                stringResource(
                    id = R.string.crypto_purchase_flow_exchange_reference_edittext,
                    (quoteAmountText.value.ifEmpty { EMPTY_CURRENCY }.toDouble() / currencyPrice).roundToEightDecimalPlaces(),
                    asset
                )
            } else {
                stringResource(
                    id = R.string.crypto_purchase_flow_exchange_reference_edittext_dollars,
                    (currencyPrice * baseAmountText.value.ifEmpty { EMPTY_CURRENCY }.toDouble()).toCurrencyFormat()
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

const val EMPTY_CURRENCY = "0.00"
