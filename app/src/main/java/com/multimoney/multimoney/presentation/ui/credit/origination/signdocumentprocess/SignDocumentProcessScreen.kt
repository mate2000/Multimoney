package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.BaseEvent.OpenWhatsAppLink
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.BaseEvent.SimulateUserInteraction
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.Companion.PHONE_HARDCODED
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnCallGetLinkCreditContractEvent
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnCallGetLinkCreditContractSecondTime
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnShowDialogInformation
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnStartListenerSubscriptionCreditContractEvent
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.documentgeneration.DocumentGenerationScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.processingtransaction.ProcessingTransactionScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.signdocument.SignDocumentScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.validateidentity.ValidateIdentityScreen
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.PROCESSING_TRANSACTION
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY
import com.multimoney.multimoney.presentation.util.openWhatsAppDeepLink
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignDocumentProcessScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SignDocumentProcessViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val whatsAppLink = stringResource(
        id = R.string.whatsapp_deep_link,
        PHONE_HARDCODED
    )

    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate, onPopBackStack = onPopBackStack)
            onUIEvent(OnCallGetLinkCreditContractEvent)
            onUIEvent(OnStartListenerSubscriptionCreditContractEvent)
            baseEvent.collectLatest { event ->
                when (event) {
                    is SimulateUserInteraction -> activity?.onUserInteraction()
                    is OpenWhatsAppLink -> context.openWhatsAppDeepLink(whatsAppLink)
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.apply {
            mmCountDownTimer.resumeTimer()
        }
    }

    when (viewModel.uiState.signDocumentProcessStep) {
        GENERATE_DOCUMENT_STEP.value -> {
            DocumentGenerationScreen(onGetLinkAgain = {
                viewModel.onUIEvent(OnCallGetLinkCreditContractSecondTime)
            })
        }
        SIGN_DOCUMENTS_STEP.value -> {
            SignDocumentScreen(viewModel = viewModel)
            LaunchedEffect(key1 = true) {
                viewModel.onUIEvent(OnShowDialogInformation)
            }
        }
        VALIDATE_IDENTITY.value -> {
            ValidateIdentityScreen(viewModel = viewModel)
        }
        PROCESSING_TRANSACTION.value -> {
            ProcessingTransactionScreen(viewModel = viewModel)
        }
    }

    if (viewModel.uiState.isAlertResultVisible) {
        viewModel.uiState.apply {
            AlertResult(
                iconResource = alertResultIconResource,
                titleResource = alertResultTitleResource,
                descriptionResource = alertResultDescriptionResource,
                buttonTextResource = alertResultButtonResource,
                isTopNavBarVisible = true,
                isRightButtonVisible = alertResultIsRightButtonVisible,
                isLeftButtonVisible = alertResultIsLeftButtonVisible,
                onRightButtonClick = alertResultRightButtonClick,
                onButtonClick = alertResultButtonAction
            )
        }
    }

    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.dialogParameters.titleResource),
            message = stringResource(id = viewModel.uiState.dialogParameters.descriptionResource).ifEmpty { viewModel.uiState.dialogParameters.description },
            positiveButtonText = stringResource(id = viewModel.uiState.dialogParameters.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.dialogParameters.negativeResource),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction
        )
    }

    // this is require to block the onBack event
    BackHandler(onBack = {})
}
