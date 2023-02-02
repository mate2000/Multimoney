package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToPaymentSmartFlow
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSendMoneyFlow
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartCtaButtons
import com.multimoney.multimoney.presentation.util.catalog.ProductType

/**
 * Composable function to show the option to active accountsmart product
 */
@Composable
fun SmartCtaFooterExpanded(
    viewModel: ProductViewModel,
    currentPage: Int,
    onLoadingValueChange: (isLoading: Boolean) -> Unit
) {
    val decrement =
        if (viewModel.uiState.productPageList?.any { it.product == ProductType.Credit.value } == true) 1 else 0
    val index = currentPage.minus(viewModel.balanceCredit?.balanceCredit?.size ?: decrement)
    SmartCtaButtons(
        modifier = Modifier.padding(16.dp).fillMaxWidth().wrapContentHeight(),
        onClickPay = {
            viewModel.onUIEvent(
                OnNavigateToPaymentSmartFlow(
                    viewModel.balanceCredit?.balanceAccountSmart?.get(index),
                    onLoadingValueChange
                )
            )
        },
        onClickSendMoney = {
            viewModel.onUIEvent(
                OnNavigateToSendMoneyFlow(
                    viewModel.balanceCredit?.balanceAccountSmart?.get(index)
                )
            )
        },
        canSendMoney = viewModel.canSendMoney(
            viewModel.uiState.productPageList?.get(currentPage)?.productSmartIndex
        )
    )
}
