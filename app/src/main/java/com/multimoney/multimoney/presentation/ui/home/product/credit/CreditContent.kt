package com.multimoney.multimoney.presentation.ui.home.product.credit

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnMaxAttemptsCardClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnProductClick
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardCreditMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardGTWithoutCredit
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardWithCreditInProcess
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditApprovedOrStarted
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditApprovedOrStartedStatus.CreditStatusApproved
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnFidoIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditStartProcessIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.OngoingCredit
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Primary

@Composable
fun CreditContent(viewModel: ProductViewModel) {
    val context = LocalContext.current
    val whatsAppLink = stringResource(
        id = R.string.whatsapp_deep_link,
        SignUpViewModel.PHONE_HARDCODED
    )

    viewModel.uiState.userStatus?.apply {
        when (infoCredit?.status) {
            CreditStatus.EXIST_IN_CORE.status -> {
                CustomProductBackground(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    type = Primary
                ) {
                    OngoingCredit(viewModel)
                }
            }
            CreditStatus.APPROVED_CREDIT.status, CreditStatus.CREDIT_PRE_APPROVED.status -> {
                CustomProductBackground(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    type = Primary
                ) {
                    when {
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_INITIAL_CARD, this) -> {
                            val infoPreApprove =
                                viewModel.uiState.userStatus?.infoCredit?.infoPreApprove?.infoProducts?.first()
                            CreditApprovedOrStarted(
                                creditApprovedOrStartedStatus = CreditStatusApproved,
                                infoPreApprove?.amountAvailableFormat,
                                viewModel.uiState.idBrand.toInt(),
                                action = {
                                    viewModel.onUIEvent(OnProductClick(whatsAppLink, context))
                                }
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_MAX_ATTEMPTS, this) -> {
                            CardCreditMaxAttempts(
                                action = {
                                    viewModel.onUIEvent(
                                        OnMaxAttemptsCardClick(
                                            whatsAppLink = whatsAppLink,
                                            context = context
                                        )
                                    )
                                }
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_IDENTITY_INCOMPLETE, this) -> {
                            CardWithCreditInProcess(
                                type = CreditProcessOnFidoIncomplete,
                                action = {
                                    viewModel.onUIEvent(OnProductClick(whatsAppLink, context))
                                }
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_INFO_INCOMPLETE, this) -> {
                            CardWithCreditInProcess(
                                type = CreditStartProcessIncomplete,
                                action = {
                                    viewModel.onUIEvent(OnNavigateToCreditScreen)
                                }
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_REJECTED, this) -> {
                            CardWithCreditInProcess(
                                type = CreditStartProcessIncomplete,
                                action = {
                                    viewModel.onUIEvent(OnProductClick(whatsAppLink, context))
                                }
                            )
                        }
                        else -> Unit
                    }
                }
            }
            CreditStatus.CREDIT_REJECTED.status, CreditStatus.CREDIT_NOT_PRE_APPROVED.status -> {
                when (viewModel.uiState.idBrand) {
                    Brand.Guatemala.id.toString() -> {
                        CustomProductBackground(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            type = Primary
                        ) {
                            CardGTWithoutCredit(action = {
                                viewModel.onUIEvent(
                                    OnProductClick(
                                        whatsAppLink,
                                        context
                                    )
                                )
                            })
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}
