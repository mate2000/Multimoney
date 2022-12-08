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
            SmartAccountStatus.NO_EXIST.status -> {
                CustomProductBackground(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    type = ProductBackGroundType.Secondary
                ){
                    when {
                        viewModel.evaluateCardCondition(ProductViewModel.SMART_INITIAL_CARD, this) -> {
                            viewModel.uiState.userStatus?.infoBankAccount?.wording.let {
                                    CardInactiveSmartProduct(
                                        it?.textOne.toString(),
                                        it?.textTwo.toString(),
                                        it?.cTA.toString()
                                    ) {
                                        viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow(ProductViewModel.SMART_INITIAL_CARD))
                                    }
                            }
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.SMART_IDENTITY_INCOMPLETE, this) -> {
                            CardWithSmartInProcess(
                                type = SmartProcessStarted.SmartProcessOnFidoIncomplete,
                                action = {
                                    viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow(ProductViewModel.SMART_IDENTITY_INCOMPLETE))
                                },
                                wording = viewModel.uiState.userStatus?.infoBankAccount?.wording
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.SMART_ONFIDO_REJECTED, this) -> {
                            CardWithSmartInProcess(
                                type = SmartProcessStarted.SmartProcessOnfidoReject,
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = {
                                    viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow(ProductViewModel.SMART_ONFIDO_REJECTED))
                                },
                                wording = viewModel.uiState.userStatus?.infoBankAccount?.wording

                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.SMART_APPROVED_BY_ONFIDO, this) -> {
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
                    }
                }

            }
        }
    }

}
