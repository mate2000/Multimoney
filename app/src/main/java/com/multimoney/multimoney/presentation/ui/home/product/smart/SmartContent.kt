package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.APPROVED
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.FAILED
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.FIRMED
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.OVER_COUNTER
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.PENDING
import com.multimoney.data.util.catalog.CreditOnFidoOrFirmStatus.REJECTED
import com.multimoney.data.util.catalog.SmartAccountStatus
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardInactiveSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardSmartProduct
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType

@Composable
fun SmartContent(viewModel: ProductViewModel, currentPage: Int) {
    val index = currentPage.minus(viewModel.balanceCredit?.balanceCredit?.size ?: 0)
    CustomProductBackground(
        modifier = Modifier.padding(horizontal = 16.dp),
        type = ProductBackGroundType.Secondary
    ) {
        if (viewModel.uiState.userStatus?.infoBankAccount?.statusFirm?.equals(PENDING.status) == true || viewModel.uiState.userStatus?.infoBankAccount?.statusFirm?.equals(
                APPROVED.status
            ) == true || viewModel.uiState.userStatus?.infoBankAccount?.statusFirm?.equals(FIRMED.status) == true || viewModel.uiState.userStatus?.infoBankAccount?.statusFirm?.equals(
                REJECTED.status
            ) == true || viewModel.uiState.userStatus?.infoBankAccount?.statusFirm?.equals(
                OVER_COUNTER.status
            ) == true || viewModel.uiState.userStatus?.infoBankAccount?.statusFirm?.equals(FAILED.status) == true
        ) {
            viewModel.uiState.userStatus?.infoBankAccount?.wording.let {
                CardInactiveSmartProduct(
                    it?.textOne.toString(),
                    it?.textTwo.toString(),
                    it?.cTA.toString()
                ) {
                    // TODO add navigation according to status
                    viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow)
                }
            }
        } else {
            when (viewModel.uiState.userStatus?.infoBankAccount?.status) {
                SmartAccountStatus.EXIST_IN_CORE.status -> {
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
                SmartAccountStatus.NO_EXIST.status -> {
                    viewModel.uiState.userStatus?.infoBankAccount?.wording.let {
                        CardInactiveSmartProduct(
                            it?.textOne.toString(),
                            it?.textTwo.toString(),
                            it?.cTA.toString()
                        ) {
                            // TODO add navigation according to status
                            viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow)
                        }
                    }
                }
            }
        }
    }
}
