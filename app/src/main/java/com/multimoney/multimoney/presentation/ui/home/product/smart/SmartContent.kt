package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardInactiveSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardSmartProduct
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType

@Composable
fun SmartContent(viewModel: ProductViewModel, currentPage: Int) {
    when (viewModel.uiState.userStatus?.infoBankAccount?.status){
        1 -> {
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
                }
            }
        }
        0 -> {
            viewModel.uiState.userStatus?.infoBankAccount?.wording.let {
                CustomProductBackground(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    type = ProductBackGroundType.Secondary
                ) {
                    CardInactiveSmartProduct(it?.textOne.toString(),it?.textTwo.toString(),it?.cTA.toString()){
                        //TODO add navigation according to status
                    }
                }
            }
        }
    }
}
