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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import com.multimoney.multimoney.presentation.util.roundToTwoDecimalPlaces
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
                baseAmount = 0.0,
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun BuyCurrencyScreenContent(
    viewModel: BuyCurrencyScreenViewModel
) {

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    //val focusManager = LocalFocusManager.current

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
                    exchangeRate = viewModel.uiState.pricesQuoteAndCommissions?.base_amount ?: 0.0,
                )
                CounterSection(
                    modifier = Modifier.constrainAs(counter) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(button.top)
                    },
                    smartAccountAvailableBalance = viewModel.smartAccountAvailableBalance,
                    downCounter = viewModel.timerCount
                )
                CustomButton(
                    modifier = Modifier
                        .constrainAs(button) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 40.dp),
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AmountInputSection(
    modifier: Modifier = Modifier,
    asset: String,
    exchangeRate: Double
) {

    val testingTest = remember { mutableStateOf("") }
    val testingBoolean = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //custom edittext
        CryptoCurrencyInputLayout(
            query = testingTest,
            focused = testingBoolean,
            iconCurrency = asset,
            onSearchClick = {},
        )
        Text(
            text = buildAnnotatedString { append("${exchangeRate.roundToTwoDecimalPlaces()} $asset") },
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
                text = buildAnnotatedString {
                    append("1 ")
                    append(asset)
                    append(" = ")
                    append(currencyPrice.toCurrencyFormat())
                },
                style = Typography.subtitle1.copy(
                    color = MultimoneyTheme.colors.text
                )
            )
        }
    }
}
