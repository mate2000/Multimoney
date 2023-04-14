package com.multimoney.multimoney.presentation.ui.home.product.credit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.credit.movements.CreditMovementsLatest
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnCreateMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToHomeMultimoneyVisa
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnShareIbanAccount
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditDetail
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditVisa
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.ScheduleAutomaticPayment

/**
 * Composable function to show the option to active accountsmart product
 */
@Composable
fun CreditFooterExpanded(viewModel: ProductViewModel, sharedViewModel: HomeViewModel) {
    Column(
        Modifier.padding(top = 16.dp).fillMaxWidth().wrapContentHeight()
    ) {
        ScheduleAutomaticPayment(viewModel, sharedViewModel)
        Spacer(modifier = Modifier.height(24.dp))
        Divider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(1.dp),
            color = MultimoneyTheme.colors.dividerWhite16
        )
        CreditVisa(
            uiState = viewModel.uiState,
            balance = viewModel.balanceCredit,
            configurationVersion = viewModel.configurationVersion,
            onNavigateToVisaActivateScreen = { viewModel.onUIEvent(OnNavigateToHomeMultimoneyVisa) },
            onCreateMultimoneyVisa = {
                viewModel.onUIEvent(
                    OnCreateMultimoneyVisa(onLoadingValueChange = {
                        sharedViewModel.onUIEvent(HomeViewModel.UIEvent.OnLoadingValueChanged(it))
                    })
                )
            },
            isExpanded = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        CreditDetail(
            modifier = Modifier
                .background(MultimoneyTheme.colors.creditDetailBackground)
                .wrapContentSize(),
            uiState = viewModel.uiState,
            balance = viewModel.balanceCredit,
            getCreditBalanceLabel = {
                viewModel.getCreditBalanceLabel(it)
            },
            getQuota = {
                viewModel.getQuota(it)
            },
            getMinPayment = { viewModel.getMinPayment(it) },
            onShareIbanAccount = { clientLabel: String, accountLabel: String, ibanAccount: String ->
                viewModel.onUIEvent(
                    OnShareIbanAccount(
                        clientLabel,
                        accountLabel,
                        ibanAccount
                    )
                )
            }
        )
        CreditMovementsLatest(viewModel)
    }
}
