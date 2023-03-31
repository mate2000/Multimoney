package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.Companion.SMART_CARD_NO_ACTION
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardInactiveSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardSmartProduct
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink

@Composable
fun SmartContent(viewModel: ProductViewModel, index: Int) {
    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(UIEvent.OnGetSmartContent)
    }

    val context = LocalContext.current

    CustomProductBackground(
        modifier = Modifier.padding(horizontal = 16.dp),
        type = ProductBackGroundType.Secondary
    ) {
        viewModel.uiState.userStatus?.apply {
            when (viewModel.uiState.smartContent.first) {
                true -> {
                    val step = viewModel.uiState.smartContent.second
                    viewModel.uiState.userStatus?.infoBankAccount?.wording.let {
                        CardInactiveSmartProduct(
                            it?.textOne.toString(),
                            it?.textTwo.toString(),
                            it?.cTA.toString(),
                            step != SMART_CARD_NO_ACTION
                        ) {
                            if (step != SMART_CARD_NO_ACTION) {
                                viewModel.onUIEvent(
                                    OnNavigateToSmartOriginationFlow(
                                        smartStep = step,
                                        onIntent = {
                                            context.openWhatsAppDeepLink(
                                                viewModel.uiState.userStatus?.infoBankAccount?.wording?.link
                                                    ?: ""
                                            )
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
                false -> {
                    viewModel.balanceCredit?.balanceAccountSmart?.let {
                        if (it.isNotEmpty()) {
                            CardSmartProduct(
                                currency = it[index]?.currencyCode ?: "",
                                profitMonthly = it[index]?.totalInterest?.toDoubleOrNull(),
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
