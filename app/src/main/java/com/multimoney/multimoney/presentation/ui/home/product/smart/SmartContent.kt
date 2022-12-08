package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.SmartAccountStatus
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardInactiveSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardWithSmartInProcess
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartProcessStarted
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType

@Composable
fun SmartContent(viewModel: ProductViewModel, currentPage: Int) {

    viewModel.uiState.userStatus?.apply {
        when (infoBankAccount?.status) {
            SmartAccountStatus.EXIST_IN_CORE.status -> {
                viewModel.balanceCredit?.balanceAccountSmart?.let {
                    if (it.isNotEmpty()) {
                        val index = currentPage.minus(viewModel.balanceCredit?.balanceCredit?.size ?: 0)
                        CustomProductBackground(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            type = ProductBackGroundType.Secondary
                        ) {
                            CardSmartProduct(
                                currency = it[index]?.currencyCode ?: "",
                                profitMonthly = it[index]?.gainedInterest.toString(),
                                profitTotal = it[index]?.totalBalance.toString()
                            )
                        }
                    }
                }
            }
            // TODO refactor to show the initial card
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
                            // TODO add navigation according to status
                            viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow("flow"))
                        }
                    }
                }
            }
            SmartAccountStatus.SMART_PRE_APPROVED.status, SmartAccountStatus.APPROVED_SMART.status -> {
                CustomProductBackground(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    type = ProductBackGroundType.Primary
                ){
                    when {
                        viewModel.evaluateCardCondition(ProductViewModel.SMART_IDENTITY_INCOMPLETE, this) -> {
                            CardWithSmartInProcess(
                                type = SmartProcessStarted.SmartStartProcessIncomplete,
                                action = {
                                    viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow(ProductViewModel.SMART_IDENTITY_INCOMPLETE))
                                },
                                wording = viewModel.uiState.userStatus?.infoBankAccount?.wording
                            )

                        }
                    }
                }
            }
        }
    }

}
