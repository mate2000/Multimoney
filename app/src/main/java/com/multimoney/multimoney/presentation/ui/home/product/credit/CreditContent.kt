package com.multimoney.multimoney.presentation.ui.home.product.credit

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.CreditInProcessStatus
import com.multimoney.data.util.catalog.CreditWorkflow
import com.multimoney.data.util.catalog.getCreditProcessStatus
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnMaxAttemptsCardClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToGtSvNonPreApproved
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardCreditFirmedAndOnfidoPending
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardGtSvCreditRejected
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardNonPreApprovedCredit
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardWithCreditInProcess
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditManualProcess
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditOfferApproved
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessCreateAccountFailure
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessFirmMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessFirmReject
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnfidoMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnfidoReject
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditStartProcessIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.OngoingCredit
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Primary

@Composable
fun CreditContent(viewModel: ProductViewModel) {
    viewModel.uiState.userStatus?.apply {
        val context = LocalContext.current
        var content: @Composable () -> Unit = {}
        var cta: String? = null
        if (infoCredit?.wording?.display == true) {
            when (infoCredit?.wording?.workflow) {
                CreditWorkflow.CREDIT_AVAILABLE.workflow -> {
                    content = {
                        OngoingCredit(viewModel)
                    }
                    cta = null
                }
                CreditWorkflow.CREDIT_PROCESS.workflow,
                CreditWorkflow.CONTACT_EVICERTIA_MAX.workflow,
                CreditWorkflow.CREDIT_ONFIDO_PROCESS.workflow,
                CreditWorkflow.CREDIT_CONTRACT_PROCESS.workflow,
                CreditWorkflow.CONTACT_ERROR.workflow -> {
                    content = {
                        CardWithCreditInProcess(
                            type = getCardWithCreditInProcessType(
                                infoCredit?.wording?.workflow ?: "",
                                infoCredit?.infoPreApprove?.status
                            ),
                            idBrand = viewModel.uiState.idBrand.toIntOrNull() ?: Brand.CostaRica.id,
                            action = getCardAction(
                                workflow = infoCredit?.wording?.workflow ?: "",
                                whatsAppLink = viewModel.whatsAppLink,
                                context = context,
                                viewModel = viewModel
                            ),
                            wording = infoCredit?.wording
                        )
                    }
                    cta = infoCredit?.wording?.cTA
                }
                CreditWorkflow.CREDIT_PENDING.workflow -> {
                    content = { CardCreditFirmedAndOnfidoPending() }
                    cta = null
                }
                CreditWorkflow.CREDIT_NOT_PREAPROVED.workflow -> {
                    content = {
                        CardNonPreApprovedCredit(
                            textOne = infoCredit?.wording?.textOne,
                            textTwo = infoCredit?.wording?.textTwo,
                            action = getCardAction(
                                workflow = infoCredit?.wording?.workflow ?: "",
                                whatsAppLink = viewModel.whatsAppLink,
                                context = context,
                                viewModel = viewModel
                            )
                        )
                    }
                    cta =
                        stringResource(id = R.string.home_product_gt_sv_non_pre_approved_credit_action)
                }
                CreditWorkflow.CREDIT_REJECTED.workflow -> {
                    content = {
                        CardGtSvCreditRejected(
                            action = getCardAction(
                                workflow = infoCredit?.wording?.workflow ?: "",
                                whatsAppLink = viewModel.uiState.userStatus?.infoCredit?.wording?.link
                                    ?: viewModel.whatsAppLink,
                                context = context,
                                viewModel = viewModel
                            ),
                            wording = viewModel.uiState.userStatus?.infoCredit?.wording
                        )
                    }
                    cta = viewModel.uiState.userStatus?.infoCredit?.wording?.cTA
                }
                CreditWorkflow.CONTACT_CREDIT_ERROR.workflow -> {
                    content = {
                        CardWithCreditInProcess(
                            type = CreditProcessCreateAccountFailure,
                            idBrand = viewModel.uiState.idBrand.toIntOrNull() ?: Brand.CostaRica.id,
                            wording = viewModel.uiState.userStatus?.infoCredit?.wording,
                            action = {
                                viewModel.onUIEvent(
                                    OnMaxAttemptsCardClick(
                                        whatsAppLink = viewModel.whatsAppLink,
                                        context = context
                                    )
                                )
                            }
                        )
                    }
                    cta = viewModel.uiState.userStatus?.infoCredit?.wording?.cTA
                }
                else -> {
                    content = {
                        CardWithCreditInProcess(
                            type = CreditManualProcess,
                            idBrand = viewModel.uiState.idBrand.toIntOrNull() ?: Brand.Default.id,
                            wording = viewModel.uiState.userStatus?.infoCredit?.wording
                        )
                    }
                }
            }
            CustomProductBackground(
                modifier = Modifier.padding(horizontal = 16.dp),
                type = Primary,
                cta = cta
            ) {
                content()
            }
        }
    }
}

fun getCardAction(
    workflow: String,
    whatsAppLink: String,
    context: Context,
    viewModel: ProductViewModel
): () -> Unit = when (workflow) {
    CreditWorkflow.CREDIT_NOT_PREAPROVED.workflow -> {
        { viewModel.onUIEvent(OnNavigateToGtSvNonPreApproved) }
    }
    CreditWorkflow.CREDIT_REJECTED.workflow,
    CreditWorkflow.CONTACT_EVICERTIA_MAX.workflow,
    CreditWorkflow.CONTACT_ERROR.workflow,
    CreditWorkflow.CONTACT_CREDIT_ERROR.workflow -> {
        {
            viewModel.onUIEvent(
                OnMaxAttemptsCardClick(
                    whatsAppLink = whatsAppLink,
                    context = context
                )
            )
        }
    }
    else -> {
        {
            viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToCreditScreen(workflow))
        }
    }
}

fun getCardWithCreditInProcessType(workflow: String, status: String? = "") = when (workflow) {
    CreditWorkflow.CREDIT_PROCESS.workflow -> {
        when(getCreditProcessStatus(status)) {
            CreditInProcessStatus.OFFER -> CreditOfferApproved
            CreditInProcessStatus.IN_PROCESS -> CreditStartProcessIncomplete
            else -> null
        }
    }
    CreditWorkflow.CONTACT_EVICERTIA_MAX.workflow -> CreditProcessFirmMaxAttempts
    CreditWorkflow.CREDIT_ONFIDO_PROCESS.workflow -> CreditProcessOnfidoReject
    CreditWorkflow.CREDIT_CONTRACT_PROCESS.workflow -> CreditProcessFirmReject
    CreditWorkflow.CONTACT_ERROR.workflow -> CreditProcessOnfidoMaxAttempts
    CreditWorkflow.CONTACT_CREDIT_ERROR.workflow -> CreditProcessCreateAccountFailure
    else -> null
}
