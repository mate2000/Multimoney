package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardOfferSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardSmartProduct
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType

@Composable
fun SmartContent(viewModel: ProductViewModel, currentPage: Int) {
    viewModel.balanceCredit?.balanceAccountSmart?.let {
        if (it.isNotEmpty()) {
            CustomProductBackground(
                modifier = Modifier.padding(horizontal = 16.dp),
                type = ProductBackGroundType.Secondary
            ) {
                CardSmartProduct(
                    currency = it[
                        currentPage.minus(
                            viewModel.balanceCredit?.balanceCredit?.size ?: 0
                        )
                    ]?.currencyCode ?: "",
                    profitMonthly = it[
                        currentPage.minus(
                            viewModel.balanceCredit?.balanceCredit?.size ?: 0
                        )
                    ]?.gainedInterest.toString(),
                    profitTotal = it[
                        currentPage.minus(
                            viewModel.balanceCredit?.balanceCredit?.size ?: 0
                        )
                    ]?.totalBalance.toString()
                )
            }
        } else {
            CustomProductBackground(
                modifier = Modifier.padding(horizontal = 16.dp),
                type = ProductBackGroundType.Secondary
            ) {
                CardOfferSmartProduct() {
                    viewModel.onUIEvent(OnNavigateToSmartOriginationFlow)
                }
            }
        }
    }
}
