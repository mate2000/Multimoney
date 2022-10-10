package com.multimoney.multimoney.presentation.ui.payment.fee

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
import androidx.navigation.NavBackStackEntry
import com.multimoney.domain.model.balance.Summary
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.payment.fee.PaymentFeeSelectionViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.payment.fee.PaymentFeeSelectionViewModel.UIEvent.OnNavigateToPaymentAccount
import com.multimoney.multimoney.presentation.ui.payment.fee.PaymentFeeSelectionViewModel.UIEvent.OnSaveArguments
import com.multimoney.multimoney.presentation.uielement.CustomInfoButton
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.Currency

@Composable
fun PaymentFeeSelectionScreen(
    navBackStackEntry: NavBackStackEntry,
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: PaymentFeeSelectionViewModel = hiltViewModel()
) {

    LaunchedEffect(true) {
        viewModel.executeNavigation(onNavigate = onNavigate, onPopAndNavigate = onPopAndNavigate)
        navBackStackEntry.arguments?.apply {
            viewModel.onUIEvent(
                OnSaveArguments(
                    getString(USER),
                    getInt(ID_BRAND),
                    getInt(ID_CLIENT),
                    getInt(ID_LOAN_CLIENT),
                    (get(SUMMARY_LIST) as Array<Summary>).toList()
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
    ) {
        TopNavBar(isRightButtonVisible = false, onLeftButtonClick = {
            viewModel.onUIEvent(OnNavigateBack)
        })
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
                items(viewModel.uiState.summaryList) { summary ->
                    CustomInfoButton(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                        startIcon = Currency.Search.getAccountIconByIdCurrency(summary.idCurrency).feeIcon,
                        title = "${stringResource(id = Currency.Search.getAccountIconByIdCurrency(summary.idCurrency).feeInfoButtonTitle)} ${summary.currency?.lowercase()}",
                        subtitle = summary.monthlyQuotaLabel ?: "",
                        onClick = {
                            viewModel.onUIEvent(OnNavigateToPaymentAccount(Currency.Search.getAccountIconByIdCurrency(summary.idCurrency)))
                        })
                }
                if (viewModel.uiState.summaryList.count() > 1) {
                    item {
                        CustomInfoButton(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                            startIcon = Currency.Search.getAccountIconByIdCurrency(Currency.All.id).feeIcon,
                            title = stringResource(id = Currency.Search.getAccountIconByIdCurrency(Currency.All.id).feeInfoButtonTitle),
                            subtitle = viewModel.getAllQuotas(stringResource(id = string.payment_fee_both_plus_symbol)),
                            onClick = {
                                viewModel.onUIEvent(OnNavigateToPaymentAccount(Currency.All))
                            })
                    }
                }
            }
        }
    }
}