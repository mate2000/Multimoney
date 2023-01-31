package com.multimoney.multimoney.presentation.ui.crypto.purchase.selectaccount

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
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType

@Composable
fun SelectSmartAccountScreen(
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    sharedViewModel: PurchaseCryptoSharedViewModel,
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
                sharedViewModel.asset,
                sharedViewModel.assetDescription
            )
        )
    }

    BackHandler { sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep) }
    SelectSmartAccountContent(viewModel, sharedViewModel) { accountToken, totalBalance ->
        sharedViewModel.onUIEvent(
            PurchaseCryptoSharedViewModel.UIEvent.OnSetSelectedAccount(
                accountToken,
                totalBalance
            )
        )
        sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnNextStep)
    }
}

@Composable
fun SelectSmartAccountContent(
    viewModel: SelectSmartAccountViewModel,
    sharedViewModel: PurchaseCryptoSharedViewModel,
    onNextStep: (String, Double) -> Unit = { _, _ -> }
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
                id = R.string.crypto_select_smart_account_title_template,
                viewModel.uiState.currencyDescription
            ),
            style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
            color = MultimoneyTheme.colors.labelText,
            textAlign = TextAlign.Left
        )
        LazyColumn {
            items(sharedViewModel.uiState.accounts) { account ->
                CustomInfoButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    startIcon = getCurrencyLogo(account.currencyCode),
                    title = stringResource(
                        id = R.string.buy_crypto_multimoney_smart_account_template,
                        account.currencyCode ?: ""
                    ),
                    onClick = {
                        onNextStep(
                            account.accountToken,
                            account.totalBalance ?: 0.0
                        )
                    }
                )
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
