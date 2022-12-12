package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.SmartAccountStatus
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardInactiveSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardSmartProduct
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType

@Composable
fun SmartContent(viewModel: ProductViewModel, currentSmartPage: Int) {
    when (viewModel.uiState.userStatus?.infoBankAccount?.status) {
        SmartAccountStatus.EXIST_IN_CORE.status -> {
            viewModel.balanceCredit?.balanceAccountSmart?.let {
                if (it.isNotEmpty()) {
                    CustomProductBackground(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        type = ProductBackGroundType.Secondary
                    ) {
                        CardSmartProduct(
                            currency = it[currentSmartPage.minus(1)]?.currencyCode ?: "",
                            profitMonthly = it[currentSmartPage.minus(1)]?.gainedInterest.toString(),
                            profitTotal = it[currentSmartPage.minus(1)]?.totalBalance.toString()
                        )
                    }
                }
            }
        }
        SmartAccountStatus.NO_EXIST.status -> {
            viewModel.uiState.userStatus?.infoBankAccount?.wording.let {
                CustomProductBackground(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    type = ProductBackGroundType.Secondary
                ) {
                    CardInactiveSmartProduct(
                        it?.textOne.toString(),
                        it?.textTwo.toString(),
                        it?.cTA.toString()
                    ) {
                        viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow(false))
                    }
                }
            }
        }
    }
}
