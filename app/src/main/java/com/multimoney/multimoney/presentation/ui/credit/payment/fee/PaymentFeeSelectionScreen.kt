package com.multimoney.multimoney.presentation.ui.credit.payment.fee

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.credit.payment.fee.PaymentFeeSelectionViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.payment.fee.PaymentFeeSelectionViewModel.UIEvent.OnNavigateToPaymentAccount
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.getCurrencyFromId

@Composable
fun PaymentFeeSelectionScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: PaymentFeeSelectionViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate, onPopBackStack = onPopBackStack)
    }
    PaymentFeeSelectionContent(viewModel)
    BackHandler {
        viewModel.onUIEvent(OnNavigateBack)
    }
}

@Composable
@Preview
fun PaymentFeeSelectionContent(
    viewModel: PaymentFeeSelectionViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(
            onLeftButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
            onRightButtonClick = { viewModel.onUIEvent(OnNavigateBack) }
        )
        Column(
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                stringResource(id = R.string.payment_fee_title),
                style = Typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.labelText
            )

            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(viewModel.uiState.summaryList ?: listOf()) { summary ->
                    CustomInfoButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        startIcon = summary?.idCurrency?.getCurrencyFromId()?.feeIcon ?: 0,
                        title = "${stringResource(id = summary?.idCurrency?.getCurrencyFromId()?.feeInfoButtonTitle ?: 0)} ${
                        stringResource(
                            id = summary?.idCurrency?.getCurrencyFromId()?.currencyName ?: string.empty
                        ).lowercase()
                        }",
                        subtitle = summary?.monthlyQuotaLabel ?: "",
                        onClick = { viewModel.onUIEvent(OnNavigateToPaymentAccount(summary?.idCurrency?.getCurrencyFromId())) }
                    )
                }
                if ((viewModel.uiState.summaryList?.count() ?: 0) > 1) {
                    item {
                        CustomInfoButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            startIcon = CurrencyType.All.feeIcon,
                            title = stringResource(id = CurrencyType.All.feeInfoButtonTitle),
                            subtitle = viewModel.getAllQuotas(stringResource(id = string.payment_fee_both_plus_symbol)),
                            onClick = {
                                viewModel.onUIEvent(OnNavigateToPaymentAccount(CurrencyType.All))
                            }
                        )
                    }
                }
            }
        }
    }
}
