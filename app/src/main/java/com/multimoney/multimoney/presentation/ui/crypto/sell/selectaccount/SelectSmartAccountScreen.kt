package com.multimoney.multimoney.presentation.ui.crypto.sell.selectaccount

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.sell.SellCryptoSharedViewModel
import com.multimoney.multimoney.presentation.ui.home.profile.accounts.MyAccountsSkeleton
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.capitalized
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType

@Composable
fun SelectSmartAccountScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    sharedViewModel: SellCryptoSharedViewModel,
    viewModel: SelectSmartAccountViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )
        viewModel.onUIEvent(
            SelectSmartAccountViewModel.UIEvent.OnSetAccounts(
                sharedViewModel.uiState.asset,
                sharedViewModel.uiState.assetDescription
            )
        )
    }

    BackHandler { sharedViewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnPreviousStep) }

    SelectSmartAccountContent(sharedViewModel) {
        sharedViewModel.onUIEvent(SellCryptoSharedViewModel.UIEvent.OnNextStep)
    }
}

@Composable
fun SelectSmartAccountContent(
    sharedViewModel: SellCryptoSharedViewModel,
    onNextStep: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            text = stringResource(
                id = R.string.sell_crypto_where_to_receive_money
            ),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        if (sharedViewModel.uiState.isLoading) {
            MyAccountsSkeleton()
        } else {
            LazyColumn {
                items(sharedViewModel.uiState.accounts) { account ->
                    CustomInfoButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        startIcon = getCurrencyLogo(account.currencyCode),
                        title = stringResource(
                            id = R.string.buy_crypto_multimoney_smart_account_template,
                            account.currencyCode?.capitalized() ?: ""
                        ),
                        onClick = {
                            sharedViewModel.onUIEvent(
                                SellCryptoSharedViewModel.UIEvent.OnSetSelectedAccount(
                                    smartAccountAvailableBalance = account.totalBalance ?: 0.0,
                                    idCurrency = account.idCurrencyAccount ?: CurrencyType.Dollar.id,
                                    accountToken = account.accountToken,
                                    accountNumber = account.accountNumber,
                                    ibanAccountNumber = account.ibanAccountNumber,
                                )
                            )
                            onNextStep()
                        }
                    )
                }
            }
        }
    }
}

fun getCurrencyLogo(currency: String?): Int? {
    return when (currency) {
        CurrencyType.Dollar.value -> R.drawable.ic_payment_dollar
        CurrencyType.Colon.value -> R.drawable.ic_payment_colon
        else -> null
    }
}