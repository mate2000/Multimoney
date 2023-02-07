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
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomCheckboxDialog
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.CustomOutlinedTextField
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator

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

@Composable
fun CryptoSendAddressContent(
    viewModel: CryptoSendAddressViewModel,
    sharedViewModel: CryptoSendSharedViewModel,
    onScanCryptoAddressClicked: () -> Unit,
) {
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
            leadingIconAlignment = Alignment.Top
        )
        // Crypto Address Text field
        CustomOutlinedTextField(
            modifier = Modifier.padding(top = 40.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { /*TODO*/ }),
            labelText = stringResource(
                id = R.string.crypto_send_address_receiver,
                sharedViewModel.uiState.asset
            ),
            value = viewModel.uiState.cryptoAddress.value,
            onValueChange = { viewModel.uiState.cryptoAddress.value = it },
            trailingIcon = R.drawable.ic_qr_code,
            trailingIconActionEnabled = true,
            trailingIconAction = { onScanCryptoAddressClicked() },
            trailingIconColor = MultimoneyTheme.colors.cryptoActionButtonEnable,
            trailingIconEndPadding = 16.dp,
            placeHolder = stringResource(id = R.string.crypto_send_address_placeholder),
            isRequired = true,
            isRequiredMessage = stringResource(id = R.string.crypto_send_address_required),
            singleLine = false,
        )
        Spacer(Modifier.weight(1f))
        // Continue Button
        CustomButton(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 40.dp)
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                viewModel.onUIEvent(CryptoSendAddressViewModel.UIEvent.OnContinueButtonClicked)
            },
            enable = viewModel.uiState.cryptoAddress.value.isNotBlank(),
        )
    }
}
