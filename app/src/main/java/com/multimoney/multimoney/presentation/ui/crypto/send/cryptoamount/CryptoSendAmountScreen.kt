package com.multimoney.multimoney.presentation.ui.crypto.send.cryptoamount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.multimoney.data.util.catalog.SendCryptoStep
import com.multimoney.data.util.catalog.TransferStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.LocalMultimoneyColors
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.AmountInputSection
import com.multimoney.multimoney.presentation.ui.crypto.NativeLoaderScreen
import com.multimoney.multimoney.presentation.ui.crypto.send.CryptoSendSharedViewModel
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.util.calculateAssetEstimated
import com.multimoney.multimoney.presentation.util.roundToEightDecimalPlaces
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CryptoSendAmountScreen(
    viewModel: CryptoSendAmountViewModel = hiltViewModel(),
    sharedViewModel: CryptoSendSharedViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(
            CryptoSendAmountViewModel.UIEvent.OnSetUserData(
                pkUser = sharedViewModel.pkUser.toInt(),
                asset = sharedViewModel.uiState.asset,
                cryptoNetwork = sharedViewModel.uiState.cryptoNetwork,
                idBrand = sharedViewModel.idBrand,
                user = sharedViewModel.email,
                identification = sharedViewModel.identification,
                assetImageUrl = sharedViewModel.uiState.assetImg,
                destinationAddress = sharedViewModel.uiState.destinationAddress,
                currentBalanceInDollar = sharedViewModel.uiState.currencyDollarBalance,
                currentCryptoBalance = calculateAssetEstimated(
                    sharedViewModel.uiState.currencyDollarBalance.toString(),
                    sharedViewModel.uiState.cryptoCurrencyPrice
                ).toDouble(),
                currencyPrice = sharedViewModel.uiState.cryptoCurrencyPrice,
                openMaintenanceAction = {
                    sharedViewModel.onUIEvent(
                        CryptoSendSharedViewModel.BaseEvent.OnShowMaintenance
                    )
                }
            )
        )
        sharedViewModel.uiState.previousAction = {
            viewModel.onUIEvent(CryptoSendAmountViewModel.UIEvent.OnClearInputData)
        }
    }
    LaunchedEffect(key1 = viewModel.uiState.isTransformationCurrency.value) {
        viewModel.onUIEvent(CryptoSendAmountViewModel.UIEvent.OnAmountChanged(""))
    }

    when(viewModel.uiState.transferStatus) {
        TransferStatus.IDLE -> {
            CryptoSendAmountScreenContent(
                sharedViewModel = sharedViewModel,
                viewModel = viewModel,
                keyboardController = keyboardController
            )
        }
        TransferStatus.LOADING -> {
            sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.LOADING))
            NativeLoaderScreen()
        }
        TransferStatus.SUCCESS -> {
            sharedViewModel.onUIEvent(
                CryptoSendSharedViewModel.UIEvent.OnSetupVoucherDetails(
                    sendCryptoAmount = "${viewModel.uiState.sendCryptoAmount.roundToEightDecimalPlaces()} ${viewModel.asset}",
                    sendDollarAmount = viewModel.uiState.sendDollarAmount,
                    transferFee = "${viewModel.uiState.transferCommission?.transferFee?.totalFee?.roundToEightDecimalPlaces()} ${viewModel.asset}",
                    referenceNumber = viewModel.uiState.referenceNumber
                )
            )
            viewModel.onUIEvent(CryptoSendAmountViewModel.UIEvent.OnRegisterAdjustSendCrypto)
            sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnNextStep)
        }
        TransferStatus.ERROR -> {
            sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.SEND_ERROR))
            AlertResult(
                titleString = stringResource(id = R.string.crypto_send_flow_error_sending_crypto),
                descriptionString = stringResource(R.string.crypto_send_flow_error_sending_crypto_try_again),
                buttonTextResource = R.string.understood,
                isRightButtonVisible = true,
                isLeftButtonVisible = false,
                onButtonClick = {
                    sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnNavigateHome)
                },
                onRightButtonClick = {
                    sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnNavigateHome)
                }
            )
        }
        TransferStatus.FAILED -> {
            sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.SEND_FAILED))
            AlertResult(
                titleString = stringResource(id = R.string.crypto_send_flow_error_oh_no),
                descriptionString = stringResource(R.string.crypto_send_flow_error_not_sent),
                buttonTextResource = R.string.error_button_try_again,
                isRightButtonVisible = true,
                isLeftButtonVisible = false,
                onButtonClick = {
                    sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.LOADING))
                    viewModel.onUIEvent(CryptoSendAmountViewModel.UIEvent.OnSendCryptoCurrency)
                },
                onRightButtonClick = {
                    sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnNavigateHome)
                }
            )
        }
    }

    if (viewModel.uiState.showErrorScreen) {
        keyboardController?.hide()
        sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnSetFlowStep(SendCryptoStep.SEND_ERROR))
        AlertResult(
            iconResource = R.drawable.ic_error_symbol,
            titleResource = R.string.error_connection_title,
            descriptionResource = R.string.error_button_try_later,
            buttonTextResource = R.string.common_go_home,
            isLeftButtonVisible = false,
            onRightButtonClick = { sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnNavigateHome) },
            onButtonClick = { sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnNavigateHome) }
        )
        BackHandler {
            sharedViewModel.onUIEvent(CryptoSendSharedViewModel.UIEvent.OnNavigateHome)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class, FlowPreview::class,
    ExperimentalCoroutinesApi::class
)
@Composable
fun CryptoSendAmountScreenContent(
    sharedViewModel: CryptoSendSharedViewModel,
    viewModel: CryptoSendAmountViewModel,
    keyboardController: SoftwareKeyboardController?
) {
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val textDebounce = remember { MutableStateFlow("") }
    val textDebounceFlow: Flow<String> = remember {
        textDebounce.debounce(CryptoSendAmountViewModel.TEXT_DEBOUNCE_TIME).flatMapLatest {
            if (it.isNotBlank()) {
                viewModel.onUIEvent(CryptoSendAmountViewModel.UIEvent.OnCalculateAmountTransferCommission)
            }
            flowOf(it)
        }
    }
    // This is required to execute the debounce
    val textDebounceFlowValue by textDebounceFlow.collectAsState("")

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            SendCryptoAmountBottomSheetScreen(
                viewModel = viewModel,
                coroutineScope = coroutineScope,
                modalBottomSheetState = modalBottomSheetState
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MultimoneyTheme.colors.background)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(
                    id = R.string.crypto_amount_title,
                    sharedViewModel.uiState.assetDescription
                ),
                style = Typography.h6.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = LocalMultimoneyColors.current.titleText,
                    lineHeight = 24.sp,
                    fontSize = 20.sp
                ),
                textAlign = TextAlign.Left
            )

            CustomInformativeText(
                leadingIcon = R.drawable.ic_information,
                text = stringResource(
                    id = R.string.crypto_amount_info_msg
                ),
                textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.titleText),
                alignmentVertical = Alignment.Top
            )

            Row(
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = rememberAsyncImagePainter(model = sharedViewModel.uiState.assetImg),
                    contentDescription = sharedViewModel.uiState.assetDescription
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = stringResource(
                        id = R.string.crypto_available_dollar_amount_to_bitcoin,
                        sharedViewModel.uiState.currencyDollarBalance,
                        calculateAssetEstimated(
                            sharedViewModel.uiState.currencyDollarBalance.toString(),
                            sharedViewModel.uiState.cryptoCurrencyPrice
                        ),
                        sharedViewModel.uiState.asset
                    ),
                    style = Typography.body2,
                    color = MultimoneyTheme.colors.titleText,
                    textAlign = TextAlign.Start
                )
            }

            AmountInputSection(
                asset = sharedViewModel.uiState.asset,
                currencyPrice = sharedViewModel.uiState.cryptoCurrencyPrice,
                isError = viewModel.uiState.isError,
                errorText = viewModel.uiState.error,
                quoteAmount = viewModel.uiState.quoteAmount,
                baseAmount = viewModel.uiState.baseAmount,
                isTransformationCurrency = viewModel.uiState.isTransformationCurrency,
                keyboardController = keyboardController,
                focusRequester = focusRequester,
                onAmountChanged = {
                    viewModel.onUIEvent(CryptoSendAmountViewModel.UIEvent.OnAmountChanged(it))
                    textDebounce.value = it
                }
            )

            Spacer(Modifier.weight(1f))

            CustomButton(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 40.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    coroutineScope.launch {
                        keyboardController?.hide()
                        modalBottomSheetState.show()
                    }
                },
                enable = viewModel.uiState.isError.not() and
                        viewModel.uiState.isLoading.not() and
                        viewModel.uiState.feeCalculated and
                        (viewModel.uiState.sendCryptoAmount > CryptoSendAmountViewModel.MINIMUM_SEND_AMOUNT)
            )
        }
    }
}