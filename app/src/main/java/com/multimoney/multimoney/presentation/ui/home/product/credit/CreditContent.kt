package com.multimoney.multimoney.presentation.ui.home.product.credit

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multimoney.data.util.catalog.CreditWorkFlow
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
                when (infoCredit?.wording?.workFlow) {
                    CreditWorkFlow.CREDIT_AVAILABLE.workFlow -> {
                        OngoingCredit(viewModel)
                    }
                    CreditWorkFlow.CREDIT_PROCESS.workFlow,
                    CreditWorkFlow.CONTACT_EVICERTIA_MAX.workFlow,
                    CreditWorkFlow.CREDIT_ONFIDO_PROCESS.workFlow,
                    CreditWorkFlow.CREDIT_CONTRACT_PROCESS.workFlow,
                    CreditWorkFlow.CONTACT_ERROR.workFlow -> {
                        CustomProductBackground(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            type = Primary
                        ) {
                            CardWithCreditInProcess(
                                type = getCardWithCreditInProcessType(infoCredit?.wording?.workFlow ?: ""),
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = getCardAction(
                                    workFlow = infoCredit?.wording?.workFlow ?: "",
                                    whatsAppLink = whatsAppLink,
                                    context = context,
                                    viewModel = viewModel
                                ),
                                wording = infoCredit?.wording
                            )
                        }
                    }
                    CreditWorkFlow.CREDIT_PENDING.workFlow -> {
                        CardCreditFirmedAndOnfidoPending()
                    }
                    CreditWorkFlow.CREDIT_NOT_PREAPROVED.workFlow -> {
                        CustomProductBackground(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            type = Primary
                        ) {
                            CardNonPreApprovedCredit(
                                idBrand = viewModel.uiState.idBrand.toInt(),
                                action = getCardAction(
                                    workFlow = infoCredit?.wording?.workFlow ?: "",
                                    whatsAppLink = whatsAppLink,
                                    context = context,
                                    viewModel = viewModel
                                )
                            )
                        }
                    }
                    CreditWorkFlow.CREDIT_REJECTED.workFlow -> {
                        CardGtSvCreditRejected(
                            action = getCardAction(
                                workFlow = infoCredit?.wording?.workFlow ?: "",
                                whatsAppLink = viewModel.uiState.userStatus?.infoCredit?.wording?.link
                                    ?: whatsAppLink,
                                context = context,
                                viewModel = viewModel
                            ),
                            wording = viewModel.uiState.userStatus?.infoCredit?.wording
                        )
                    }
                    CreditWorkFlow.CONTACT_CREDIT_ERROR.workFlow -> {
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
    workFlow: String,
    whatsAppLink: String,
    context: Context,
    viewModel: ProductViewModel
): () -> Unit = when (workFlow) {
    CreditWorkFlow.CREDIT_NOT_PREAPROVED.workFlow -> {
        { viewModel.onUIEvent(OnNavigateToGtSvNonPreApproved) }
    }
    CreditWorkFlow.CONTACT_EVICERTIA_MAX.workFlow,
    CreditWorkFlow.CONTACT_ERROR.workFlow,
    CreditWorkFlow.CONTACT_CREDIT_ERROR.workFlow -> {
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
            viewModel.onUIEvent(ProductViewModel.UIEvent.OnNavigateToCreditScreen(workFlow))
        }
    }
}

fun getCardWithCreditInProcessType(workFlow: String) = when (workFlow) {
    CreditWorkFlow.CREDIT_PROCESS.workFlow -> CreditStartProcessIncomplete
    CreditWorkFlow.CONTACT_EVICERTIA_MAX.workFlow -> CreditProcessFirmMaxAttempts
    CreditWorkFlow.CREDIT_ONFIDO_PROCESS.workFlow -> CreditProcessOnfidoReject
    CreditWorkFlow.CREDIT_CONTRACT_PROCESS.workFlow -> CreditProcessFirmReject
    CreditWorkFlow.CONTACT_ERROR.workFlow -> CreditProcessOnfidoMaxAttempts
    CreditWorkFlow.CONTACT_CREDIT_ERROR.workFlow -> CreditProcessCreateAccountFailure
    else -> null
}
