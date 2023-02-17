package com.multimoney.multimoney.presentation.ui.home.product.credit

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.CreditWorkflow
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnMaxAttemptsCardClick
import com.multimoney.multimoney.presentation.ui.home.product.ProductViewModel.UIEvent.OnNavigateToGtSvNonPreApproved
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardCreditFirmedAndOnfidoPending
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardGtSvCreditRejected
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardNonPreApprovedCredit
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CardWithCreditInProcess
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessCreateAccountFailure
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessFirmMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessFirmReject
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnfidoMaxAttempts
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditProcessOnfidoReject
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.CreditProcessStarted.CreditStartProcessIncomplete
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.OngoingCredit
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel
import com.multimoney.multimoney.presentation.uielement.CustomProductBackground
import com.multimoney.multimoney.presentation.uielement.ProductBackGroundType.Primary

@Composable
fun CreditContent(viewModel: ProductViewModel) {
    viewModel.uiState.userStatus?.apply {
        val context = LocalContext.current
        val whatsAppLink = stringResource(
            id = R.string.whatsapp_deep_link,
            SignUpViewModel.PHONE_HARDCODED
        )
        if (infoCredit?.wording?.display == true) {
            CustomProductBackground(
                modifier = Modifier.padding(horizontal = 16.dp),
                type = Primary
            ) {
                when (infoCredit?.wording?.workflow) {
                    CreditWorkflow.CREDIT_AVAILABLE.workflow -> {
                        OngoingCredit(viewModel)
                    }
                    CreditWorkflow.CREDIT_PROCESS.workflow,
                    CreditWorkflow.CONTACT_EVICERTIA_MAX.workflow,
                    CreditWorkflow.CREDIT_ONFIDO_PROCESS.workflow,
                    CreditWorkflow.CREDIT_CONTRACT_PROCESS.workflow,
                    CreditWorkflow.CONTACT_ERROR.workflow -> {
                        CustomProductBackground(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            type = Primary
                        ) {
                            CardWithCreditInProcess(
                                type = getCardWithCreditInProcessType(infoCredit?.wording?.workflow ?: ""),
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = getCardAction(
                                    workflow = infoCredit?.wording?.workflow ?: "",
                                    whatsAppLink = whatsAppLink,
                                    context = context,
                                    viewModel = viewModel
                                ),
                                wording = infoCredit?.wording
                            )
                        }
                    }
                    CreditWorkflow.CREDIT_PENDING.workflow -> {
                        CardCreditFirmedAndOnfidoPending()
                    }
                    CreditWorkflow.CREDIT_NOT_PREAPROVED.workflow -> {
                        CustomProductBackground(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            type = Primary
                        ) {
                            CardNonPreApprovedCredit(
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = getCardAction(
                                    workflow = infoCredit?.wording?.workflow ?: "",
                                    whatsAppLink = whatsAppLink,
                                    context = context,
                                    viewModel = viewModel
                                )
                            )
                        }
                    }
                    CreditWorkflow.CREDIT_REJECTED.workflow -> {
                        CardGtSvCreditRejected(
                            action = getCardAction(
                                workflow = infoCredit?.wording?.workflow ?: "",
                                whatsAppLink = viewModel.uiState.userStatus?.infoCredit?.wording?.link
                                    ?: whatsAppLink,
                                context = context,
                                viewModel = viewModel
                            ),
                            wording = viewModel.uiState.userStatus?.infoCredit?.wording
                        )
                    }
                    CreditWorkflow.CONTACT_CREDIT_ERROR.workflow -> {
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

fun getCardWithCreditInProcessType(workflow: String) = when (workflow) {
    CreditWorkflow.CREDIT_PROCESS.workflow -> CreditStartProcessIncomplete
    CreditWorkflow.CONTACT_EVICERTIA_MAX.workflow -> CreditProcessFirmMaxAttempts
    CreditWorkflow.CREDIT_ONFIDO_PROCESS.workflow -> CreditProcessOnfidoReject
    CreditWorkflow.CREDIT_CONTRACT_PROCESS.workflow -> CreditProcessFirmReject
    CreditWorkflow.CONTACT_ERROR.workflow -> CreditProcessOnfidoMaxAttempts
    CreditWorkflow.CONTACT_CREDIT_ERROR.workflow -> CreditProcessCreateAccountFailure
    else -> null
}
