package com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.BuyCryptoStep
import com.multimoney.data.util.catalog.PurchaseStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.crypto.AmountInputSection
import com.multimoney.multimoney.presentation.ui.crypto.CounterSection
import com.multimoney.multimoney.presentation.ui.crypto.NativeLoaderScreen
import com.multimoney.multimoney.presentation.ui.crypto.TitleSection
import com.multimoney.multimoney.presentation.ui.crypto.VoucherCurrencyExchangeInfoSkeleton
import com.multimoney.multimoney.presentation.ui.crypto.WhileLoadingSection
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CurrencyExchangeInfo
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.calculateAmountPlusFee
import com.multimoney.multimoney.presentation.util.calculateConfirmationBaseAmount
import com.multimoney.multimoney.presentation.util.calculateConfirmationQuoteAmount
import com.multimoney.multimoney.presentation.util.calculateConvertedCurrencyBalance
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.toCurrencyFormat
import kotlinx.coroutines.launch

const val DEFAULT_CURRENCY_PRICE = 0.0
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
                accountToken = sharedViewModel.uiState.accountToken.toLong() ?: 0L,
                side = sharedViewModel.side,
                assetImageUrl = sharedViewModel.uiState.assetImageBaseUrl,
                smartAccountAvailableBalance = sharedViewModel.uiState.smartAccountAvailableBalance ?: 0.0,
                ibanAccountNumber = sharedViewModel.uiState.ibanAccountNumber,
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
    when (viewModel.uiState.purchaseStatus) {
        PurchaseStatus.IDLE -> {
            BuyCurrencyScreenContent(viewModel)
        }
        PurchaseStatus.LOADING -> {
            sharedViewModel.onUIEvent(
                PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(
                    BuyCryptoStep.LOADING_SCREEN
                )
            )
            NativeLoaderScreen()
        }
        PurchaseStatus.SUCCESS -> {
            sharedViewModel.onUIEvent(
                PurchaseCryptoSharedViewModel.UIEvent.OnSetupVoucherDetails(
                    quoteAmount = calculateConfirmationQuoteAmount(
                        quoteAmount = viewModel.uiState.quoteAmount.value,
                        baseAmount = viewModel.uiState.baseAmount.value,
                        currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
                    ),
                    baseAmount = calculateConfirmationBaseAmount(
                        quoteAmount = viewModel.uiState.quoteAmount.value,
                        baseAmount = viewModel.uiState.baseAmount.value,
                        currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
                    ),
                    totalDebitedAmount = calculateAmountPlusFee(
                        amount = calculateConfirmationQuoteAmount(
                            quoteAmount = viewModel.uiState.quoteAmount.value,
                            baseAmount = viewModel.uiState.baseAmount.value,
                            currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
                        ).drop(1),
                        fee = viewModel.uiState.pricesQuoteAndCommissions?.totalFee
                    ).toCurrencyFormat(),
                    exchangeRate = viewModel.uiState.exchangeRate.toCurrencyFormat(
                        symbol = CurrencyType.Colon.symbol
                    ),
                    totalDebitedExchange = calculateConvertedCurrencyBalance(
                        quoteAmount = viewModel.uiState.quoteAmount.value,
                        baseAmount = viewModel.uiState.baseAmount.value,
                        exchangeRate = viewModel.uiState.exchangeRate,
                        price = viewModel.uiState.pricesQuoteAndCommissions?.price
                            ?: 0.0
                    ),
                    referenceNumber = viewModel.uiState.referenceNumber ?: ""
                )
            )
            sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnNextStep)
        }
        PurchaseStatus.FAILED -> {
            sharedViewModel.onUIEvent(
                PurchaseCryptoSharedViewModel.UIEvent.OnSetFlowStep(
                    BuyCryptoStep.PURCHASE_FAILED
                )
            )
            AlertResult(
                titleString = stringResource(id = R.string.crypto_purchase_flow_error_processing_purchase),
                descriptionString = stringResource(R.string.common_sorry_try_again_later),
                buttonTextResource = R.string.profile_error_changing_phone_button,
                isRightButtonVisible = true,
                isLeftButtonVisible = false,
                onButtonClick = {
                    sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnNavigateHome)
                },
                onRightButtonClick = {
                    sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnNavigateHome)
                }
            )
        }
    }
    BackHandler {
        sharedViewModel.uiState.previousAction()
        sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep)
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
    viewModel: BuyCurrencyScreenViewModel
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
                viewModel = viewModel
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
                        title = if (viewModel.idBrand == Brand.CostaRica.id) {
                            R.string.crypto_purchase_flow_title_cr
                        } else {
                            R.string.crypto_purchase_flow_title_sv
                        },
                        cryptoAssetExchange = stringResource(
                            id = R.string.crypto_purchase_flow_exchange_reference,
                            viewModel.asset,
                            viewModel.uiState.pricesQuoteAndCommissions?.price
                                ?: DEFAULT_CURRENCY_PRICE.toCurrencyFormat()
                        ),
                        imageUrl = viewModel.assetImageUrl,
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
                        counterTextResourceId = R.string.crypto_purchase_flow_price_expires_in,
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
