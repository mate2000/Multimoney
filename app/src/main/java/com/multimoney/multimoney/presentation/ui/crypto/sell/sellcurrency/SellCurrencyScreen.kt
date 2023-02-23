package com.multimoney.multimoney.presentation.ui.crypto.sell.sellcurrency

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.multimoney.data.util.catalog.SellCryptoStep
import com.multimoney.data.util.catalog.SellStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.crypto.AmountInputSection
import com.multimoney.multimoney.presentation.ui.crypto.CounterSection
import com.multimoney.multimoney.presentation.ui.crypto.NativeLoaderScreen
import com.multimoney.multimoney.presentation.ui.crypto.TitleSection
import com.multimoney.multimoney.presentation.ui.crypto.sell.SellCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.calculateAvailableInDollars
import com.multimoney.multimoney.presentation.util.calculateConfirmationBaseAmount
import com.multimoney.multimoney.presentation.util.calculateConfirmationQuoteAmount
import com.multimoney.multimoney.presentation.util.calculateConvertedCurrencyBalance
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.roundToEightDecimalPlaces
import com.multimoney.multimoney.presentation.util.toCurrencyFormat
import kotlinx.coroutines.launch

const val DEFAULT_CURRENCY_PRICE = 0.0
const val TIMER_UNIT_INDICATOR = " seg"
const val WHITE_SPACE = " "

@Composable
fun SellCurrencyScreen(
    sharedViewModel: SellCryptoSharedViewModel = hiltViewModel(),
    viewModel: SellCurrencyScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(
            SellCurrencyScreenViewModel.UIEvent.OnSetUserData(
                idCurrencyAccount = sharedViewModel.uiState.idCurrency,
                pkUser = sharedViewModel.pkUser.toInt(),
                asset = sharedViewModel.uiState.asset ?: "",
                cryptoNetwork = sharedViewModel.uiState.cryptoNetWork,
                idBrand = sharedViewModel.idBrand,
                user = sharedViewModel.user,
                market = sharedViewModel.uiState.market,
                identification = sharedViewModel.identification,
                accountToken = sharedViewModel.uiState.accountToken.toLong(),
                side = sharedViewModel.side,
                assetImageUrl = sharedViewModel.uiState.assetImageBaseUrl,
                ibanAccountNumber = sharedViewModel.uiState.ibanAccountNumber,
                cryptoAvailableBalance = sharedViewModel.uiState.assetAvailable ?: 0.0,
                openMaintenanceAction = {
                    sharedViewModel.onUIEvent(
                        SellCryptoSharedViewModel.BaseEvent.OnShowMaintenance
                    )
                }
            )
        )
        sharedViewModel.uiState.previousAction = {
            viewModel.onUIEvent(SellCurrencyScreenViewModel.UIEvent.OnClearInputData)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(SellCurrencyScreenViewModel.UIEvent.OnGetQuoteAndCommissions)
        viewModel.onUIEvent(SellCurrencyScreenViewModel.UIEvent.ValidateAmountInput(""))
        viewModel.onUIEvent(
            SellCurrencyScreenViewModel.UIEvent.OnSetFailureAction(
                failureAction = {
                    sharedViewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnPreviousStep)
                }
            )
        )
    }

    when (viewModel.uiState.sellStatus) {
        SellStatus.IDLE -> {
            SellCurrencyScreenContent(viewModel, sharedViewModel = sharedViewModel)
        }
        SellStatus.LOADING -> {
            sharedViewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(SellCryptoStep.LOADING_SCREEN)
            )
            NativeLoaderScreen()
        }
        SellStatus.SUCCESS -> {
            sharedViewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetupVoucherDetails(
                    assetAmount = calculateConfirmationBaseAmount(
                        quoteAmount = viewModel.uiState.quoteAmount.value,
                        baseAmount = viewModel.uiState.baseAmount.value,
                        currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
                    ).plus(" ${viewModel.asset}"),
                    approximateValue = calculateConfirmationQuoteAmount(
                        quoteAmount = viewModel.uiState.quoteAmount.value,
                        baseAmount = viewModel.uiState.baseAmount.value,
                        currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price,
                        exchangeRate = viewModel.uiState.exchangeRate,
                        symbol = if (viewModel.idCurrencyAccount == CurrencyType.Dollar.id) {
                            CurrencyType.Dollar.symbol
                        } else {
                            CurrencyType.Colon.symbol
                        }
                    ),
                    totalCreditedAmount = calculateConfirmationQuoteAmount(
                        quoteAmount = viewModel.uiState.quoteAmount.value,
                        baseAmount = viewModel.uiState.baseAmount.value,
                        currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price,
                        symbol = CurrencyType.Dollar.symbol,
                        totalFee = viewModel.uiState.pricesQuoteAndCommissions?.totalFee ?: 0.0
                    ),
                    exchangeRate = viewModel.uiState.exchangeRate.toCurrencyFormat(
                        symbol = CurrencyType.Colon.symbol
                    ),
                    totalCreditedAmountExchange  = calculateConvertedCurrencyBalance(
                        quoteAmount = viewModel.uiState.quoteAmount.value,
                        baseAmount = viewModel.uiState.baseAmount.value,
                        price = viewModel.uiState.pricesQuoteAndCommissions?.price,
                        exchangeRate = viewModel.uiState.exchangeRate,
                        totalFee = viewModel.uiState.pricesQuoteAndCommissions?.totalFee ?: 0.0
                    ),
                    referenceNumber = viewModel.uiState.referenceNumber ?: ""
                )
            )
            sharedViewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnNextStep)
        }
        SellStatus.FAILED -> {
            sharedViewModel.onUIEvent(
                SellCryptoSharedViewModel.UIEvent.OnSetFlowStep(
                    SellCryptoStep.PURCHASE_FAILED
                )
            )
            AlertResult(
                titleString = stringResource(id = R.string.crypto_sell_flow_error_processing_sell),
                descriptionString = stringResource(R.string.common_sorry_try_again_later),
                buttonTextResource = R.string.profile_error_changing_phone_button,
                isRightButtonVisible = true,
                isLeftButtonVisible = false,
                onButtonClick = {
                    sharedViewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnNavigateHome)
                },
                onRightButtonClick = {
                    sharedViewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnNavigateHome)
                }
            )
        }
    }


    BackHandler {
        sharedViewModel.uiState.previousAction()
        sharedViewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnPreviousStep)
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
fun SellCurrencyScreenContent(
    viewModel: SellCurrencyScreenViewModel,
    sharedViewModel: SellCryptoSharedViewModel
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
            SellConfirmationBottomSheet(
                modalBottomSheetState = modalBottomSheetState,
                coroutineScope = coroutineScope,
                viewModel = viewModel,
                sharedViewModel = sharedViewModel
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
                val (title, disclaimer, amountInput, counter, button) = createRefs()
                Box(modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(disclaimer.top)
                }) {
                    TitleSection(
                        title = if (viewModel.idBrand == Brand.CostaRica.id) {
                            R.string.crypto_sell_flow_sell_screen_title_cr
                        } else {
                            R.string.crypto_sell_flow_sell_screen_title_sv
                        },
                        cryptoAssetExchange = stringResource(
                            id = R.string.crypto_sell_flow_sell_screen_available_equal_to,
                            calculateAvailableInDollars(
                                baseAmount = viewModel.cryptoAvailableCurrencyBalance,
                                currencyPrice = viewModel.uiState.pricesQuoteAndCommissions?.price
                                    ?: DEFAULT_CURRENCY_PRICE
                            ).toCurrencyFormat(),
                            viewModel.cryptoAvailableCurrencyBalance.roundToEightDecimalPlaces(),
                            viewModel.asset
                        ),
                        imageUrl = viewModel.assetImageUrl,
                        isLoading = viewModel.uiState.isLoading,
                        showSellDisclaimer = true
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
                    isError = viewModel.uiState.focusError,
                    errorText = viewModel.uiState.error,
                    textArg = viewModel.uiState.errorMessageArg,
                    onAmountChanged = {
                        viewModel.onUIEvent(
                            SellCurrencyScreenViewModel.UIEvent.ValidateAmountInput(it)
                        )
                    }
                )
                Box(modifier = Modifier.constrainAs(counter) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(button.top)
                }) {
                    CounterSection(
                        counterTextResourceId = R.string.crypto_sell_flow_confirmation_sell_screen_expires_in,
                        showAvailableSmartAmount = false,
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
                            viewModel.onUIEvent(SellCurrencyScreenViewModel.UIEvent.OnOpenSellConfirmationBottomSheet)
                            modalBottomSheetState.show()
                        }
                    }
                )
            }
        }
    }
}
