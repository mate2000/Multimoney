package com.multimoney.multimoney.presentation.ui.home.product.credit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToDisbursement
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToPaymentProcess
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditCtaButtons

/**
 * Composable function to show the option to active credit product
 */
@Composable
fun CreditCtaFooterExpanded(viewModel: ProductViewModel, sharedViewModel: HomeViewModel) {
    CreditCtaButtons(
        modifier = Modifier
            .padding(16.dp).fillMaxWidth().wrapContentHeight(),
        onClickPay = { viewModel.onUIEvent(OnNavigateToPaymentProcess) },
        onClickDisbursement = { viewModel.onUIEvent(OnNavigateToDisbursement) },
        canDisburse = viewModel.uiState.canExpandCredit
    )
}
