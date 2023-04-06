package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.SmartWorkflow
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.Companion.SMART_CARD_NO_ACTION
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToSmartOriginationFlow
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardInactiveSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartProcessStarted
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink

@Composable
fun SmartContent(viewModel: ProductViewModel, index: Int) {
    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(UIEvent.OnGetSmartContent)
    }

    val context = LocalContext.current

    viewModel.uiState.userStatus?.apply {
        when (viewModel.uiState.smartContent.first) {
            true -> {
                val step = viewModel.uiState.smartContent.second
                viewModel.uiState.userStatus?.infoBankAccount?.wording.let {
                    CustomProductBackground(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        type = ProductBackGroundType.Secondary,
                        cta = if (step != SMART_CARD_NO_ACTION) it?.cTA.toString() else null
                    ) {
                        CardInactiveSmartProduct(
                            it?.textOne.toString(),
                            it?.textTwo.toString(),
                            getSmartProcessType(step)
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
            }
            false -> {
                viewModel.balanceCredit?.balanceAccountSmart?.let {
                    CustomProductBackground(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        type = ProductBackGroundType.Secondary
                    ) {
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
            }
            else -> {
                // Empty on purpose
            }
        }
    }

}

fun getSmartProcessType(workflow: String) = when (workflow) {
    SmartWorkflow.SMART_INITIAL_CARD.workflow -> SmartProcessStarted.SmartInitialProcess
    SmartWorkflow.SMART_ONFIDO_PROCESS.workflow,
    SmartWorkflow.SMART_STEP_PENDING.workflow -> SmartProcessStarted.SmartStartProcessIncomplete
    SmartWorkflow.SMART_IDENTITY_INCOMPLETE_OR_ONFIDO_MAX_ATTEMPTS.workflow ->
        SmartProcessStarted.SmartProcessOnFidoIncomplete
    SmartWorkflow.SMART_CONTRACT_PROCESS.workflow -> SmartProcessStarted.SmartProcessFirmIncomplete
    SmartWorkflow.SMART_FIRMED_ONFIDO_REJECTED.workflow -> SmartProcessStarted.SmartProcessOnfidoReject
    else -> null
}
