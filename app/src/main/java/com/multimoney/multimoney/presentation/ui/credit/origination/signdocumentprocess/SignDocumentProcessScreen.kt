package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnCallSubscriptionCreditContractEvent
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnShowDialogInformation
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.documentgeneration.DocumentGenerationScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.signdocument.SignDocumentScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.validateidentity.ValidateIdentityScreen
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.SIGN_DOCUMENTS_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.VALIDATE_IDENTITY

@Composable
fun SignDocumentProcessScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignDocumentProcessViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate)
            onUIEvent(OnCallSubscriptionCreditContractEvent)
        }
    }

    when (viewModel.uiState.signDocumentProcessStep) {
        GENERATE_DOCUMENT_STEP.value -> {
            DocumentGenerationScreen(viewModel = viewModel)
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
    }

    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.dialogParameters.titleResource),
            message = viewModel.uiState.dialogParameters.description,
            positiveButtonText = stringResource(id = viewModel.uiState.dialogParameters.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.dialogParameters.negativeResource),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction
        )
    }

    // this is require to block the onBack event
    BackHandler(onBack = {})
}
