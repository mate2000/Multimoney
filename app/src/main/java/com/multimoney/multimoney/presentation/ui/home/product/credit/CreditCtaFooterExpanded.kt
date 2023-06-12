package com.multimoney.multimoney.presentation.ui.home.product.credit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
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
    // Check if the user can disburse or has payments available
    if (viewModel.uiState.canExpandCredit || viewModel.uiState.paymentAvailable) {
        CreditCtaButtons(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            onClickPay = {
                viewModel.onUIEvent(OnNavigateToPaymentProcess)
            },
            onClickDisbursement = { viewModel.onUIEvent(OnNavigateToDisbursement) },
            canDisburse = viewModel.uiState.canExpandCredit,
            paymentAvailable = viewModel.uiState.paymentAvailable,
            idBrand = viewModel.uiState.idBrand.toIntOrNull() ?: Brand.CostaRica.id
        )
    }
}
