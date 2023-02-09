package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardInactiveSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardSmartProduct
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType
import com.multimoney.multimoney.presentation.util.catalog.ProductType
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink

@Composable
fun SmartContent(viewModel: ProductViewModel, currentPage: Int, whatsAppLink: String = "") {
    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(UIEvent.OnGetSmartContent)
    }

    val context = LocalContext.current

    val decrement =
        if (viewModel.uiState.productPageList?.any { it.product == ProductType.Credit.value } == true) 1 else 0
    val index = currentPage.minus(viewModel.balanceCredit?.balanceCredit?.size ?: decrement)
    CustomProductBackground(
        modifier = Modifier.padding(horizontal = 16.dp),
        type = ProductBackGroundType.Secondary
    ) {
        viewModel.uiState.userStatus?.apply {
            when (viewModel.uiState.smartContent.first) {
                true -> {
                    viewModel.uiState.userStatus?.infoBankAccount?.wording.let {
                        CardInactiveSmartProduct(
                            it?.textOne.toString(),
                            it?.textTwo.toString(),
                            it?.cTA.toString()
                        ) {
                            viewModel.onUIEvent(
                                OnNavigateToSmartOriginationFlow(
                                    smartStep = viewModel.uiState.smartContent.second,
                                    onIntent = { context.openWhatsAppDeepLink(viewModel.uiState.userStatus?.infoBankAccount?.wording?.link ?: "") }
                                )
                            )
                        }
                    }
                }
                false -> {
                    viewModel.balanceCredit?.balanceAccountSmart?.let {
                        if (it.isNotEmpty()) {
                            CardSmartProduct(
                                currency = it[index]?.currencyCode ?: "",
                                profitMonthly = it[index]?.gainedInterest.toString(),
                                profitTotal = it[index]?.totalBalance,
                                currentMonth = it[index]?.month ?: ""
                            )
                        }
                    }
                }
                else -> {
                    // Empty on purpose
                }
            }
        }
    }
}
