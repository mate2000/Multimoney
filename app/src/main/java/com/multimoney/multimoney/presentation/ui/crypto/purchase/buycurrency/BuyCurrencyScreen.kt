package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
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
import com.multimoney.multimoney.presentation.ui.crypto.NativeLoaderScreen
import com.multimoney.multimoney.presentation.ui.crypto.PurchaseConfirmationBottomSheet
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CryptoCurrencyInputLayout
import com.multimoney.multimoney.presentation.uielement.CurrencyExchangeInfo
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.calculateAssetEstimated
import com.multimoney.multimoney.presentation.util.calculateConvertedCurrencyBalance
import com.multimoney.multimoney.presentation.util.calculateDollarEstimated
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.toCurrencyFormat
import kotlinx.coroutines.launch

const val DEFAULT_CURRENCY_PRICE = 0.0
const val TIMER_UNIT_INDICATOR = " seg"
const val WHITE_SPACE = " "
const val EMPTY_CURRENCY = "0.00"

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
                accountToken = sharedViewModel.uiState.accounts.firstOrNull()?.accountToken?.toLong()
                    ?: 0L,
                side = sharedViewModel.side,
                assetImageUrl = sharedViewModel.uiState.assetImageBaseUrl,
                smartAccountAvailableBalance = sharedViewModel.uiState.smartAccountAvailableBalance
            )
        )
        sharedViewModel.uiState.previousAction = {
            viewModel.onUIEvent(BuyCurrencyScreenViewModel.UIEvent.OnClearInputData)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(BuyCurrencyScreenViewModel.UIEvent.OnGetQuoteAndCommissions)
        if (sharedViewModel.uiState.idCurrency == CurrencyType.Colon.id) {
            viewModel.onUIEvent(BuyCurrencyScreenViewModel.UIEvent.OnGetExchangeRate)
        }
        viewModel.onUIEvent(BuyCurrencyScreenViewModel.UIEvent.ValidateAmountInput(""))
        viewModel.onUIEvent(
            BuyCurrencyScreenViewModel.UIEvent.OnSetFailureAction(
                failureAction = {
                    sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep)
                }
            )
        )
    }

    if (viewModel.uiState.isPurchaseLoading) {
        NativeLoaderScreen()
    } else {
        BuyCurrencyScreenContent(viewModel) {
            //todo go to next step / set data etc
        }
    }

    BackHandler {
        sharedViewModel.uiState.previousAction()
        sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep)
    }

    if (viewModel.uiState.isPurchaseSuccess) {
        sharedViewModel.onUIEvent(
            PurchaseCryptoSharedViewModel.UIEvent.OnSetupVoucherDetails(
                viewModel.uiState.amountInUSD.toString(),
                viewModel.uiState.referenceNumber ?: ""
            )
        )
        sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnNextStep)
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            message = viewModel.uiState.openDialog.description.ifBlank {
                stringResource(viewModel.uiState.openDialog.descriptionResource)
            },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onNegativeAction = viewModel.uiState.openDialog.negativeAction,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun BuyCurrencyScreenContent(
    viewModel: BuyCurrencyScreenViewModel,
    onConfirm: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            PurchaseConfirmationBottomSheet(
                modalBottomSheetState = modalBottomSheetState,
                coroutineScope = coroutineScope,
                viewModel = viewModel,
                onConfirm = onConfirm
            )
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
                Box(modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(conversionCurrencyToDollars.top)
                }) {
                    TitleSection(
                        imageUrl = viewModel.assetImageUrl,
                        currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
                            ?: DEFAULT_CURRENCY_PRICE,
                        asset = viewModel.asset,
                        isLoading = viewModel.uiState.isLoading
                    )
                }
                AmountInputSection(
                    modifier = Modifier.constrainAs(amountInput) {
                        top.linkTo(title.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    keyboardController = keyboardController,
                    focusRequester = focusRequester,
                    asset = viewModel.asset,
                    currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
                        ?: DEFAULT_CURRENCY_PRICE,
                    quoteAmount = viewModel.uiState.quoteAmount,
                    baseAmount = viewModel.uiState.baseAmount,
                    isTransformationCurrency = viewModel.uiState.isTransformationCurrency,
                    isError = viewModel.uiState.isError,
                    errorText = viewModel.uiState.error,
                    textArg = viewModel.uiState.errorMessageArg,
                    onAmountChanged = {
                        viewModel.onUIEvent(
                            BuyCurrencyScreenViewModel.UIEvent.ValidateAmountInput(it)
                        )
                    }
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
                        WhileLoadingSection(
                            isLoading = viewModel.uiState.isLoading,
                            contentLoading = { VoucherCurrencyExchangeInfoSkeleton() },
                            content = {
                                CurrencyExchangeInfo(
                                    leftTitleResource = R.string.crypto_purchase_flow_exchange_type_title,
                                    rightTitleResource = R.string.crypto_purchase_flow_exchange_total_title,
                                    exchangeRateText = viewModel.uiState.exchangeRate.toCurrencyFormat(
                                        symbol = CurrencyType.Colon.symbol
                                    ),
                                    convertedAmountText = calculateConvertedCurrencyBalance(
                                        quoteAmount = viewModel.uiState.quoteAmount.value,
                                        baseAmount = viewModel.uiState.baseAmount.value,
                                        exchangeRate = viewModel.uiState.exchangeRate,
                                        price = viewModel.uiState.pricesQuoteAndCommissions?.price
                                            ?: 0.0
                                    )
                                )
                            }
                        )
                    }
                }
                Box(modifier = Modifier.constrainAs(counter) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(button.top)
                }) {
                    CounterSection(
                        smartAccountAvailableBalance = viewModel.smartAccountAvailableBalance,
                        downCounter = viewModel.uiState.remainingTimeText
                    )
                }
                CustomButton(
                    modifier = Modifier
                        .constrainAs(button) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    enable = viewModel.uiState.isError.not() and viewModel.uiState.isLoading.not(),
                    onClick = {
                        coroutineScope.launch {
                            keyboardController?.hide()
                            viewModel.onUIEvent(BuyCurrencyScreenViewModel.UIEvent.OnOpenPurchaseConfirmationBottomSheet)
                            modalBottomSheetState.show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WhileLoadingSection(
    isLoading: Boolean,
    contentLoading: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    if (isLoading) {
        contentLoading()
    } else {
        content()
    }
}

@Composable
fun CounterSection(
    modifier: Modifier = Modifier,
    downCounter: String,
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
                    append(WHITE_SPACE)
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
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
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
                    append(WHITE_SPACE)
                    withStyle(
                        style = SpanStyle(
                            color = MultimoneyTheme.colors.bodyTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(downCounter)
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
    @StringRes errorText: Int,
    textArg: Any? = null,
    quoteAmount: MutableState<String>,
    baseAmount: MutableState<String>,
    isTransformationCurrency: MutableState<Boolean>,
    keyboardController: SoftwareKeyboardController?,
    focusRequester: FocusRequester,
    onAmountChanged: (String) -> Unit
) {
    val quoteAmountText = remember { quoteAmount }
    val baseAmountText = remember { baseAmount }
    val isTransformationCurrencyValue = remember { isTransformationCurrency }
    val baseOrQuote =
        if (isTransformationCurrencyValue.value.not()) quoteAmountText else baseAmountText

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
            errorText = getTextFromStringRes(textRes = errorText, arg = textArg),
            isTransformationCurrency = isTransformationCurrencyValue,
            onValueChanged = onAmountChanged,
            onImeClick = { keyboardController?.hide() }
        )
        Text(
            text = if (isTransformationCurrencyValue.value.not()) {
                stringResource(
                    id = R.string.crypto_purchase_flow_exchange_reference_edittext,
                    calculateAssetEstimated(
                        quoteAmount = quoteAmountText.value,
                        currencyPrice = currencyPrice
                    ),
                    asset
                )
            } else {
                stringResource(
                    id = R.string.crypto_purchase_flow_exchange_reference_edittext_dollars,
                    calculateDollarEstimated(
                        baseAmount = baseAmountText.value,
                        currencyPrice = currencyPrice
                    )
                )
            },
            style = Typography.body2.copy(
                color = MultimoneyTheme.colors.bodyTextColor
            )
        )
    }
}

@Composable
private fun getTextFromStringRes(@StringRes textRes: Int, arg: Any? = null): String {
    return if (arg != null) {
        stringResource(id = textRes, arg)
    } else {
        stringResource(id = textRes)
    }
}

@Composable
fun TitleSection(
    modifier: Modifier = Modifier,
    imageUrl: String,
    asset: String,
    currencyPrice: Double,
    isLoading: Boolean
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
        WhileLoadingSection(
            isLoading = isLoading,
            contentLoading = { BuyCurrencyTitleSectionSkeleton() }
        ) {
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
}
