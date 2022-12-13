package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardInactiveSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardSmartProduct
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType

@Composable
fun SmartContent(viewModel: ProductViewModel, currentPage: Int) {
    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(UIEvent.OnGetSmartContent)
    }
    val index = currentPage.minus(1)
    CustomProductBackground(
        modifier = Modifier.padding(horizontal = 16.dp),
        type = ProductBackGroundType.Secondary
    ) {
        if (viewModel.uiState.smartContent == true) {
            viewModel.uiState.userStatus?.infoBankAccount?.wording.let {
                CardInactiveSmartProduct(
                    it?.textOne.toString(),
                    it?.textTwo.toString(),
                    it?.cTA.toString()
                ) {
                    viewModel.onUIEvent(
                        OnNavigateToSmartOriginationFlow(
                            false
                        )
                    )
                }
            }
        } else if (viewModel.uiState.smartContent == false) {
            viewModel.balanceCredit?.balanceAccountSmart?.let {
                if (it.isNotEmpty()) {
                    CardSmartProduct(
                        currency = it[index]?.currencyCode ?: "",
                        profitMonthly = it[index]?.gainedInterest.toString(),
                        profitTotal = it[index]?.totalBalance.toString()
                    )
                }
            }
        }
    }
}
