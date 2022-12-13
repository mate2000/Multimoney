package com.multimoney.multimoney.presentation.ui.credit.payment.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountViewModel.UIEvent.OnCallQueryGetClientBankAccount
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.CustomImage
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrency
import com.multimoney.multimoney.presentation.util.getMaskedAccount

@Composable
fun PaymentAccountScreen(
    isRestart: Boolean = true,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentAccountViewModel = hiltViewModel()
) {
    // Navigation
    viewModel.apply {
        isOnRestart = isRestart
        LaunchedEffect(isOnRestart) {
            if (isOnRestart) {
                executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
                onUIEvent(OnCallQueryGetClientBankAccount)
                isOnRestart = false
            }
        }
    }
    PaymentAccountContent(viewModel)
}

@Composable
@Preview
fun PaymentAccountContent(
    viewModel: PaymentAccountViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateBackHome) }
        )
        Text(
            modifier = Modifier.padding(top = 42.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
            text = stringResource(id = viewModel.uiState.titleResource),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        if (viewModel.uiState.isClientBankAccountListEmpty) {
            PaymentAccountEmptyState(viewModel)
        } else {
            PaymentAccountList(viewModel)
        }

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
    LoadingIndicator(viewModel.uiState.isLoading)
}

@Composable
@Preview
fun PaymentAccountEmptyState(
    viewModel: PaymentAccountViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.wrapContentSize())
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomImage(
                drawableResource = R.drawable.ic_bank
            )
            Text(
                text = stringResource(id = R.string.payment_account_empty_state_description),
                modifier = Modifier.padding(vertical = 25.dp, horizontal = 58.dp),
                style = Typography.body1,
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Center
            )
        }
        CustomButton(
            onClick = { viewModel.onUIEvent(OnAddAccountClick) },
            text = stringResource(id = R.string.payment_account_create),
            modifier = Modifier
                .padding(vertical = 40.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            buttonType = CustomButtonType.PrimaryPrimary
        )
    }
}

@Composable
@Preview
fun PaymentAccountList(
    viewModel: PaymentAccountViewModel = hiltViewModel()
) {
    viewModel.uiState.clientBankAccountList?.let { clientBankAccountList ->
        LazyColumn(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
            items(clientBankAccountList) { clientBankAccount ->
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    startIcon = clientBankAccount?.idCurrency?.getCurrency()?.accountIcon ?: 0,
                    title = clientBankAccount?.bankDescription ?: "",
                    subtitle = getMaskedAccount(
                        clientBankAccount?.accountNumber ?: "",
                        stringResource(id = R.string.payment_account_masked_text)
                    ),
                    onClick = {
                        viewModel.onUIEvent(OnClientBankAccountSelected(clientBankAccount))
                    }
                )
            }
        }
    }
    CustomButton(
        text = stringResource(id = R.string.payment_account_create),
        modifier = Modifier
            .padding(top = 32.dp)
            .fillMaxWidth(),
        onClick = { viewModel.onUIEvent(OnAddAccountClick) },
        buttonType = CustomButtonType.PrimaryTertiary,
        trailingIcon = R.drawable.ic_plus
    )
}
