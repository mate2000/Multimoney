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
import com.multimoney.data.util.catalog.PurchaseCryptoSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.util.NavEvent

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
                sharedViewModel.uiState.accounts,
                sharedViewModel.asset,
                sharedViewModel.assetDescription
            )
        )
        if (sharedViewModel.comingFromDetails) {
            sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnQueryAccounts)
        }
    }
    sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnSetNavigation(
        nextAction = {},
        nextStep = PurchaseCryptoSteps.Three.id,
        previousStep = PurchaseCryptoSteps.One.id,
        overridePreviousAction = { sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep) }
    ))

    BackHandler { sharedViewModel.onUIEvent(PurchaseCryptoSharedViewModel.UIEvent.OnPreviousStep) }
    SelectSmartAccountContent(viewModel) { accountToken, totalBalance ->
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
    onNextStep: (String, Double) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .background(MultimoneyTheme.colors.background)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {

        //ToDo replace with incoming currency
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
        LazyColumn() {
            items(viewModel.uiState.accounts) { account ->
                //TODO replace with real info from accounts
                CustomInfoButton(
                    modifier = Modifier.fillMaxWidth(),
                    startIcon = R.drawable.ic_multimoney_green_logo,
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