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
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.crypto.AmountInputSection
import com.multimoney.multimoney.presentation.ui.crypto.CounterSection
import com.multimoney.multimoney.presentation.ui.crypto.TitleSection
import com.multimoney.multimoney.presentation.ui.crypto.sell.SellCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomDialog
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
                accountToken = sharedViewModel.uiState.accounts.firstOrNull()?.accountToken?.toLong()
                    ?: 0L,
                side = sharedViewModel.side,
                assetImageUrl = sharedViewModel.uiState.assetImageBaseUrl,
                ibanAccountNumber = sharedViewModel.uiState.ibanAccountNumber,
                smartAccountAvailableBalance = sharedViewModel.uiState.smartAccountAvailableBalance
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
            ))
    }

    SellCurrencyScreenContent(viewModel)
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
    viewModel: SellCurrencyScreenViewModel
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
                            viewModel.cryptoAvailableCurrencyBalance.toCurrencyFormat(),
                            viewModel.cryptoAvailableCurrencyBalance
                                .div(viewModel.uiState.pricesQuoteAndCommissions?.price
                                ?: DEFAULT_CURRENCY_PRICE).roundToEightDecimalPlaces(),
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
                    isError = viewModel.uiState.isError,
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
                        smartAccountAvailableBalance = viewModel.cryptoAvailableCurrencyBalance,
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