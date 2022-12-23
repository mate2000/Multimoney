package com.multimoney.multimoney.presentation.ui.smart.payment.iban

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.smart.payment.iban.SmartTransferIbanViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.smart.payment.iban.SmartTransferIbanViewModel.UIEvent.OnCallQueryListSinpeAccountUseCaseImpl
import com.multimoney.multimoney.presentation.ui.smart.payment.iban.SmartTransferIbanViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun SmartTransferIbanScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SmartTransferIbanViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.onUIEvent(OnCallQueryListSinpeAccountUseCaseImpl)
        viewModel.executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
    }

    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            isRightButtonVisible = false
        )
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = stringResource(string.payment_account_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        Text(
            text = stringResource(id = string.smart_iban_transfer_title),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        PaymentOptions(viewModel)
    }

    LoadingIndicator(viewModel.uiState.isLoading)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}

@Composable
fun PaymentOptions(viewModel: SmartTransferIbanViewModel = hiltViewModel()) {
    LazyColumn(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
        items(viewModel.uiState.sinpeAccountList ?: listOf()) { account ->
            CustomInfoButton(
                title = account?.nameAccount ?: "",
                subtitle = stringResource(
                    id = string.smart_account_beneficiary_content,
                    account?.bank ?: "",
                    "${
                        getMaskedAccountIban(
                            account?.sinpeAccount ?: "",
                            stringResource(id = string.payment_account_masked_text)
                        )
                    }%"
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                endIcon = drawable.ic_options,
                startIcon = account?.currencyId?.getCurrencyFromId()?.accountIcon,
                onClick = {

                }
            )
        }
    }

    CustomButton(
        text = stringResource(id = string.payment_account_create),
        modifier = Modifier
            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        onClick = {
            viewModel.onUIEvent(OnAddAccountClick)
        },
        buttonType = PrimaryTertiary,
        trailingIcon = drawable.ic_plus
    )
}