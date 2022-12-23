package com.multimoney.multimoney.presentation.ui.smart.payment.accounts

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryTertiary
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getCurrencyFromId
import com.multimoney.multimoney.presentation.util.getMaskedAccountIban

@Composable
fun SmartPaymentAccountsScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartPaymentAccountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
    }

    Column(
        Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            isRightButtonVisible = false
        )
        Text(
            modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
            text = stringResource(string.payment_account_title),
            style = Typography.h5.copy(
                fontWeight = FontWeight.SemiBold,
                color = MultimoneyTheme.colors.text
            )
        )
        PaymentOptions(viewModel)
    }
}

@Composable
fun PaymentOptions(
    viewModel: SmartPaymentAccountViewModel = hiltViewModel()
) {
    LazyColumn(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
        items(viewModel.uiState.sinpeAccountList ?: listOf()) { account ->
            CustomInfoButton(
                title = account.bank,
                subtitle = getMaskedAccountIban(
                    account.sinpeAccount,
                    stringResource(id = string.payment_account_masked_text)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                endIcon = drawable.ic_right_chevron,
                startIcon = account.currencyId.getCurrencyFromId().accountIcon,
                onClick = {
                    viewModel.onUIEvent(UIEvent.OnAccountClick(account))
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
            viewModel.onUIEvent(UIEvent.OnAddAccountClick)
        },
        buttonType = PrimaryTertiary,
        trailingIcon = drawable.ic_plus,
        enable = (viewModel.uiState.sinpeAccountList?.size
            ?: 0) < DisbursementAccountViewModel.MAX_ACCOUNT_NUMBER
    )
}
