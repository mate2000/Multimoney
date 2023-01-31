package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

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
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.PurchaseCryptoSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.PurchaseConfirmationBottomSheet
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.util.roundToTwoDecimalPlaces
import com.multimoney.multimoney.presentation.util.toCurrencyFormat

@Composable
fun BuyCurrencyScreen(
    sharedViewModel: PurchaseCryptoSharedViewModel = hiltViewModel(),
    viewModel: BuyCurrencyScreenViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetNavigation(
            nextAction = {},
            nextStep = when {
                sharedViewModel.idBrand == Brand.ElSalvador.id -> PurchaseCryptoSteps.Three.id
                sharedViewModel.idBrand == Brand.ElSalvador.id
                        && sharedViewModel.comingFromDetails -> PurchaseCryptoSteps.Two.id
                sharedViewModel.idBrand == Brand.CostaRica.id
                        && sharedViewModel.comingFromDetails -> PurchaseCryptoSteps.Three.id
                else -> PurchaseCryptoSteps.Four.id
            },
            previousStep = when {
                sharedViewModel.idBrand == Brand.CostaRica.id
                        && sharedViewModel.comingFromDetails.not() -> PurchaseCryptoSteps.Two.id
                else -> PurchaseCryptoSteps.One.id
            },
            overridePreviousAction = { sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep) }
        ))
        viewModel.onUIEvent(
            BuyCurrencyScreenViewModel.UIEvent.OnSetUserData(
                asset = sharedViewModel.asset ?: "",
                cryptoNetwork = sharedViewModel.cryptoNetWork,
                idBrand = sharedViewModel.idBrand,
                user = sharedViewModel.user,
                market = sharedViewModel.market ?: "",
                identification = sharedViewModel.identification,
                baseAmount = 0.0,
                side = sharedViewModel.side,
                assetImageUrl = sharedViewModel.assetImageBaseUrl,
                smartAccountAvailableBalance = sharedViewModel.uiState.smartAccountAvailableBalance,
            )
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(BuyCurrencyScreenViewModel.UIEvent.OnGetExchangeRate)
    }

    BuyCurrencyScreenContent(viewModel)
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
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.show()

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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                //val (title, conversionCurrencyToDollars, amountInput, availableBalance, counter, button) = createRefs()
                TitleSection(
                    /*modifier = Modifier.constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(conversionCurrencyToDollars.top)
                    }*/
                )
                ConversionCurrencySection(
                    /*modifier = Modifier.constrainAs(
                        conversionCurrencyToDollars
                    ) {
                        top.linkTo(title.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(amountInput.top)
                    },*/
                    imageUrl = viewModel.assetImageUrl,
                    currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price?: 0.0,
                    asset = viewModel.asset
                )
                AmountInputSection(
                    /*modifier = Modifier.constrainAs(amountInput) {
                        top.linkTo(conversionCurrencyToDollars.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },*/
                    asset = viewModel.asset,
                    exchangeRate = viewModel.uiState.pricesQuoteAndCommissions?.base_amount ?: 0.0,
                )
                AvailableBalanceSection(
                    /*modifier = Modifier.constrainAs(availableBalance) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(counter.top)
                    },*/
                    smartAccountAvailableBalance = viewModel.smartAccountAvailableBalance
                )
                CounterSection(
                    /*modifier = Modifier.constrainAs(counter) {
                        top.linkTo(availableBalance.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(button.top)
                    },*/
                    downCounter = viewModel.timerCount
                )
                CustomButton(
                    modifier = Modifier
                        .fillMaxWidth()
                    /*.constrainAs(button) {
                        top.linkTo(availableBalance.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }*/
                )
            }
        }
    }
}

@Composable
fun CounterSection(
    modifier: Modifier = Modifier,
    downCounter: Int
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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

@Composable
fun AvailableBalanceSection(
    modifier: Modifier = Modifier,
    smartAccountAvailableBalance: Double
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
}

@Composable
fun AmountInputSection(
    modifier: Modifier = Modifier,
    asset: String,
    exchangeRate: Double
) {

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //custom edittext
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = "",
            placeholder = { Text(text = "BTC") },
            onValueChange = {}
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
fun ConversionCurrencySection(
    modifier: Modifier = Modifier,
    imageUrl: String,
    asset: String,
    currencyPrice: Double
) {
    Row(
        modifier = modifier
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

@Composable
fun TitleSection(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
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
}
