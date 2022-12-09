package com.multimoney.multimoney.presentation.ui.home.product.smart

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.SmartAccountStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardInactiveSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardSmartFirmedAndOnfidoPending
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardSmartProduct
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.CardWithSmartInProcess
import com.multimoney.multimoney.presentation.ui.home.product.smart.uisections.SmartProcessStarted
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType

@Composable
fun SmartContent(viewModel: ProductViewModel, currentPage: Int) {
    val context = LocalContext.current
    val whatsAppLink = stringResource(
        id = R.string.whatsapp_deep_link,
        SignUpViewModel.PHONE_HARDCODED
    )

    viewModel.uiState.userStatus?.apply {
        when (infoBankAccount?.status) {
            SmartAccountStatus.EXIST_IN_CORE.status -> {
                viewModel.balanceCredit?.balanceAccountSmart?.let {
                    if (it.isNotEmpty()) {
                        val smartIndex = viewModel.uiState.productPageList?.get(currentPage)?.productSmartIndex ?:0
                        //val index = currentPage.minus(viewModel.balanceCredit?.balanceCredit?.size ?: 0)
                        CustomProductBackground(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            type = ProductBackGroundType.Secondary
                        ) {
                            CardSmartProduct(
                                currency = it[smartIndex]?.currencyCode ?: "",
                                profitMonthly = it[smartIndex]?.gainedInterest.toString(),
                                profitTotal = it[smartIndex]?.totalBalance.toString()
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

                        viewModel.evaluateCardCondition(ProductViewModel.SMART_FIRMED_ONFIDO_PENDING, this) -> {
                            CardSmartFirmedAndOnfidoPending()
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
                        viewModel.evaluateCardCondition(ProductViewModel.SMART_ONFIDO_MAX_ATTEMPTS, this) -> {
                            CardWithSmartInProcess(
                                type = SmartProcessStarted.SmartProcessOnfidoMaxAttempts,
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = {
                                    viewModel.onUIEvent(
                                        ProductViewModel.UIEvent.OnMaxAttemptsCardClick(
                                            whatsAppLink = whatsAppLink,
                                            context = context
                                        )
                                    )
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
