package com.multimoney.multimoney.presentation.ui.home.profile.accounts

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.getMaskedAccount


@Composable
fun MyAccountsScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: MyAccountsViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack, onNavigate = onNavigate)
    }
    BackHandler {
        //viewModel.onUIEvent(PersonalInfoViewModel.UIEvent.OnNavigateBack)
    }
    MyAccountsContent(
        favoriteAccounts = viewModel.uiState.favoriteAccounts,
        registeredAccounts = viewModel.uiState.registeredAccounts
    )
}

@Preview
@Composable
fun MyAccountsContent(
    favoriteAccounts: List<SinpeAccount?> = listOf(),
    registeredAccounts: List<SinpeAccount?> = listOf()
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .fillMaxSize()
    ) {
        TopNavBar(
            onLeftButtonClick = {},
            isRightButtonVisible = false
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = R.string.profile_my_accounts),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText,
                textAlign = TextAlign.Left
            )
            LazyColumn {
                item {
                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        text = stringResource(id = R.string.profile_accounts_favorites),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText,
                        textAlign = TextAlign.Left
                    )
                }
                items(favoriteAccounts) { account ->
                    CustomInfoButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        startIcon = R.drawable.ic_bank_account,
                        endIcon = R.drawable.ic_options,
                        title = account?.nameAccount ?: "",
                        subtitle = getMaskedAccount(
                            account?.sinpeAccount ?: "",
                            stringResource(id = R.string.payment_account_masked_text)
                        ),
                        onClick = {
                            //viewModel.onUIEvent(PaymentAccountViewModel.UIEvent.OnClientBankAccountSelected(clientBankAccount))
                        }
                    )
                }
                item {
                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        text = stringResource(id = R.string.profile_accounts_registered),
                        style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
                        color = MultimoneyTheme.colors.labelText,
                        textAlign = TextAlign.Left
                    )
                }
                items(registeredAccounts) { account ->
                    CustomInfoButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        startIcon = R.drawable.ic_bank_account,
                        endIcon = R.drawable.ic_options,
                        title = account?.nameAccount ?: "",
                        subtitle = getMaskedAccount(
                            account?.sinpeAccount ?: "",
                            stringResource(id = R.string.payment_account_masked_text)
                        ),
                        onClick = {
                            //viewModel.onUIEvent(PaymentAccountViewModel.UIEvent.OnClientBankAccountSelected(clientBankAccount))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Accounts(modifier: Modifier, headerTextResource: Int, items: List<SinpeAccount?>) {
    Column(modifier) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(id = headerTextResource),
            style = Typography.body2.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        LazyColumn {

            items(items) { account ->
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    startIcon = R.drawable.ic_bank_account,
                    endIcon = R.drawable.ic_options,
                    title = account?.nameAccount ?: "",
                    subtitle = getMaskedAccount(
                        account?.sinpeAccount ?: "",
                        stringResource(id = R.string.payment_account_masked_text)
                    ),
                    onClick = {
                        //viewModel.onUIEvent(PaymentAccountViewModel.UIEvent.OnClientBankAccountSelected(clientBankAccount))
                    }
                )
            }
        }
    }
}
