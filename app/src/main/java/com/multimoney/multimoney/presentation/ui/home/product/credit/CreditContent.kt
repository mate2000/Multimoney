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
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnIsSwipeEnabledValueChange
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnMaxAttemptsCardClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToCreditScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToGtSvNonPreApproved
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardCreditFirmedAndOnfidoPending
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardGtSvCreditRejected
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardNonPreApprovedCredit
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardWithCreditInProcess
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditPreApproved
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessCreateAccountFailure
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessFirmIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessFirmMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessFirmReject
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnFidoIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnfidoMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnfidoReject
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
    viewModel.onUIEvent(OnIsSwipeEnabledValueChange(false))
    viewModel.uiState.userStatus?.apply {
        when (infoCredit?.status) {
            CreditStatus.EXIST_IN_CORE.status -> {
                viewModel.onUIEvent(OnIsSwipeEnabledValueChange(true))
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
                            CreditPreApproved(
                                infoPreApprove?.amountAvailableFormat,
                                viewModel.uiState.idBrand.toInt(),
                                action = {
                                    viewModel.onUIEvent(OnNavigateToCreditScreen(ProductViewModel.CREDIT_INITIAL_CARD))
                                },
                                wording = viewModel.uiState.userStatus?.infoCredit?.wording
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_INFO_INCOMPLETE, this) -> {
                            CardWithCreditInProcess(
                                type = CreditStartProcessIncomplete,
                                action = {
                                    viewModel.onUIEvent(OnNavigateToCreditScreen(ProductViewModel.CREDIT_INFO_INCOMPLETE))
                                },
                                wording = viewModel.uiState.userStatus?.infoCredit?.wording
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_FIRM_MAX_ATTEMPTS, this) -> {
                            CardWithCreditInProcess(
                                type = CreditProcessFirmMaxAttempts,
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = {
                                    viewModel.onUIEvent(
                                        OnMaxAttemptsCardClick(
                                            whatsAppLink = whatsAppLink,
                                            context = context
                                        )
                                    )
                                },
                                wording = viewModel.uiState.userStatus?.infoCredit?.wording
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_ONFIDO_REJECTED, this) -> {
                            CardWithCreditInProcess(
                                type = CreditProcessOnfidoReject,
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = {
                                    viewModel.onUIEvent(OnNavigateToCreditScreen(ProductViewModel.CREDIT_ONFIDO_REJECTED))
                                },
                                wording = viewModel.uiState.userStatus?.infoCredit?.wording
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_FIRM_REJECTED, this) -> {
                            CardWithCreditInProcess(
                                type = CreditProcessFirmReject,
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = {
                                    viewModel.onUIEvent(OnNavigateToCreditScreen(ProductViewModel.CREDIT_FIRM_REJECTED))
                                },
                                wording = viewModel.uiState.userStatus?.infoCredit?.wording
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_ONFIDO_MAX_ATTEMPTS, this) -> {
                            CardWithCreditInProcess(
                                type = CreditProcessOnfidoMaxAttempts,
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = {
                                    viewModel.onUIEvent(
                                        OnMaxAttemptsCardClick(
                                            whatsAppLink = whatsAppLink,
                                            context = context
                                        )
                                    )
                                },
                                wording = viewModel.uiState.userStatus?.infoCredit?.wording
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_IDENTITY_INCOMPLETE, this) -> {
                            CardWithCreditInProcess(
                                type = CreditProcessOnFidoIncomplete,
                                action = {
                                    viewModel.onUIEvent(OnNavigateToCreditScreen(ProductViewModel.CREDIT_IDENTITY_INCOMPLETE))
                                },
                                wording = viewModel.uiState.userStatus?.infoCredit?.wording
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_EL_SALVADOR_MANUAL_PROCESS, this) -> {
                            CardCreditFirmedAndOnfidoPending()
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_PEP_PROCESS, this) -> {
                            CardCreditFirmedAndOnfidoPending()
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_FIRM_INCOMPLETE, this) -> {
                            CardWithCreditInProcess(
                                type = CreditProcessFirmIncomplete,
                                action = {
                                    viewModel.onUIEvent(OnNavigateToCreditScreen(ProductViewModel.CREDIT_FIRM_INCOMPLETE))
                                },
                                wording = viewModel.uiState.userStatus?.infoCredit?.wording
                            )
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_FIRMED_ONFIDO_PENDING, this) -> {
                            CardCreditFirmedAndOnfidoPending()
                        }
                        viewModel.evaluateCardCondition(ProductViewModel.CREDIT_ERROR_CREATE_ACCOUNT, this) -> {
                            CardWithCreditInProcess(
                                type = CreditProcessCreateAccountFailure,
                                idBrand = viewModel.uiState.idBrand.toInt(),
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
                        else -> Unit
                    }
                }
            }
            CreditStatus.CREDIT_REJECTED.status -> {
                if (viewModel.uiState.userStatus?.infoCredit?.wording?.display == true) {
                    CustomProductBackground(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        type = Primary
                    ) {
                        CardGtSvCreditRejected(
                            action = {
                                viewModel.onUIEvent(
                                    OnMaxAttemptsCardClick(
                                        whatsAppLink = viewModel.uiState.userStatus?.infoCredit?.wording?.link
                                            ?: whatsAppLink,
                                        context = context
                                    )
                                )
                            },
                            wording = viewModel.uiState.userStatus?.infoCredit?.wording
                        )
                    }
                }
            }
            CreditStatus.CREDIT_NOT_PRE_APPROVED.status -> {
                when (viewModel.uiState.idBrand) {
                    Brand.Guatemala.id.toString(), Brand.ElSalvador.id.toString() -> {
                        CustomProductBackground(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            type = Primary
                        ) {
                            CardNonPreApprovedCredit(
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = {
                                    viewModel.onUIEvent(OnNavigateToGtSvNonPreApproved)
                                }
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}
