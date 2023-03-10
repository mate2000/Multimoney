package com.multimoney.multimoney.presentation.ui.crypto.send.cryptoaddress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.LocalMultimoneyColors
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.send.CryptoSendSharedViewModel
import com.multimoney.multimoney.presentation.ui.crypto.send.cryptoaddress.CryptoSendAddressViewModel.Companion.TEXT_DEBOUNCE_TIME
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomCheckboxDialog
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@Preview
@Composable
fun CryptoSendAddressPreview() {
    CryptoSendAddressScreen(sharedViewModel = hiltViewModel(), qrCodeResult = "")
}

@Composable
fun CryptoSendAddressScreen(
    viewModel: CryptoSendAddressViewModel = hiltViewModel(),
    sharedViewModel: CryptoSendSharedViewModel,
    onNavigateToQrCodeScanner: () -> Unit = {},
    qrCodeResult: String
) {

    LaunchedEffect(true) {
        viewModel.onUIEvent(
            CryptoSendAddressViewModel.UIEvent.OnGetInfo(
                user = sharedViewModel.email,
                idBrand = sharedViewModel.idBrand,
                identification = sharedViewModel.identification,
                market = sharedViewModel.uiState.asset,
            )
        )
        viewModel.onUIEvent(CryptoSendAddressViewModel.UIEvent.GetNotShowAgainCryptoAddressFromSharedPref)
        viewModel.onUIEvent(
            CryptoSendAddressViewModel.UIEvent.OnSetQrCodeFromSavedState(
                qrCodeResult
            )
        )
        viewModel.onUIEvent(
            CryptoSendAddressViewModel.UIEvent.OnSetOpenMaintenanceAction(
                action = {
                    sharedViewModel.onUIEvent(
                        CryptoSendSharedViewModel.BaseEvent.OnShowMaintenance
                    )
                }
            )
        )
    }

    CryptoSendAddressContent(
        viewModel = viewModel,
        sharedViewModel = sharedViewModel,
        onScanCryptoAddressClicked = onNavigateToQrCodeScanner
    )

    LoadingIndicator(viewModel.uiState.isLoading)

    if (viewModel.uiState.continueDialog.isActive.value) {
        CustomCheckboxDialog(
            title = stringResource(id = viewModel.uiState.continueDialog.titleResource),
            message = viewModel.uiState.continueDialog.description.ifBlank {
                stringResource(viewModel.uiState.continueDialog.descriptionResource)
            },
            checkboxText = stringResource(id = R.string.common_not_show_again),
            positiveButtonText = stringResource(id = viewModel.uiState.continueDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.continueDialog.negativeResource),
            openDialogCustom = viewModel.uiState.continueDialog.isActive,
            onPositiveAction = viewModel.uiState.continueDialog.positiveAction,
            isCancelable = viewModel.uiState.continueDialog.isCancelable,
        )
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction,
            onDismissAction = viewModel.uiState.openDialog.dismissAction
        )
    }
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Composable
fun CryptoSendAddressContent(
    viewModel: CryptoSendAddressViewModel,
    sharedViewModel: CryptoSendSharedViewModel,
    onScanCryptoAddressClicked: () -> Unit,
) {
    val textDebounce = remember { MutableStateFlow("") }
    val textDebounceFlow: Flow<String> = remember {
        textDebounce.debounce(TEXT_DEBOUNCE_TIME)
            .flatMapLatest {
                if (it.isNotBlank()) {
                    viewModel.onUIEvent(
                        CryptoSendAddressViewModel.UIEvent.OnValidateCryptoAddress(it)
                    )
                }
                flowOf(it)
            }
    }

    // This is required to execute the debounce
    val textDebounceFlowValue by textDebounceFlow.collectAsState("")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(16.dp),
    ) {
        // Title
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(
                id = R.string.crypto_send_address_title,
                sharedViewModel.uiState.assetDescription
            ),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = LocalMultimoneyColors.current.titleText
            ),
            textAlign = TextAlign.Left
        )
        // Information
        CustomInformativeText(
            modifier = Modifier.padding(top = 8.dp),
            leadingIcon = R.drawable.ic_information,
            text = stringResource(
                id = R.string.crypto_send_address_information,
                sharedViewModel.uiState.asset
            ),
            textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.titleText),
            alignmentVertical = Alignment.Top
        )
        // Crypto Address Text field
        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 40.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                viewModel.onUIEvent(CryptoSendAddressViewModel.UIEvent.OnContinueButtonClicked {})
            }),
            labelText = stringResource(
                id = R.string.crypto_send_address_receiver,
                sharedViewModel.uiState.asset
            ),
            value = viewModel.uiState.cryptoAddress.value,
            onValueChange = {
                viewModel.onUIEvent(CryptoSendAddressViewModel.UIEvent.OnCryptoAddressChanged(it))
                textDebounce.value = it
                sharedViewModel.uiState.destinationAddress = it
            },
            trailingIcon = R.drawable.ic_qr_code,
            trailingIconActionEnabled = true,
            trailingIconAction = { onScanCryptoAddressClicked() },
            trailingIconColor = MultimoneyTheme.colors.cryptoActionButtonEnable,
            trailingIconEndPadding = 16.dp,
            placeHolder = stringResource(id = R.string.crypto_send_address_placeholder),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.crypto_send_address_required, viewModel.uiState.asset),
            singleLine = false,
            maxLines = 2,
            isError = viewModel.uiState.showTextInputError,
            errorMessage = if (viewModel.uiState.showTextInputError) {
                stringResource(
                    id = R.string.crypto_send_address_wallet_doesnt_accept,
                    sharedViewModel.uiState.asset
                )
            } else {
                null
            },
        )
        Spacer(Modifier.weight(1f))
        // Continue Button
        CustomButton(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 40.dp)
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                viewModel.onUIEvent(CryptoSendAddressViewModel.UIEvent.OnContinueButtonClicked {
                    sharedViewModel.onUIEvent(
                        CryptoSendSharedViewModel.UIEvent.OnNextStep
                    )
                })
            },
            enable = viewModel.uiState.cryptoAddress.value.isNotBlank() &&
                    !viewModel.uiState.showTextInputError && !viewModel.uiState.isLoading,
        )
    }
}
