package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.documentgeneration.DocumentGenerationScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.processingtransaction.ProcessingTransactionScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.signdocument.SignDocumentScreen
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.validateidentity.ValidateIdentityScreen
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.CONTINUE_VALIDATING_IDENTITY
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.EVICERTIA_REJECTED_FIRST_TIME_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.EVICERTIA_REJECTED_SECOND_TIME_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.GENERATE_DOCUMENT_STEP
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.ONFIDO_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.ONFIDO_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.SignDocumentStep.PROCESSING_THE_TRANSACTION
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
            createDialog()
        }
    }

    when (viewModel.uiState.signDocumentProcessStep) {
        GENERATE_DOCUMENT_STEP.value -> {
            DocumentGenerationScreen(viewModel = viewModel)
        }
        SIGN_DOCUMENTS_STEP.value -> {
            SignDocumentScreen(viewModel = viewModel)
        }
        EVICERTIA_REJECTED_FIRST_TIME_STEP.value -> {
            AlertResult(
                iconResource = drawable.ic_alert,
                titleResource = string.sign_document_reject_title,
                descriptionResource = if (viewModel.idBrand == Brand.Guatemala.id) string.sign_document_reject_description_gt else string.sign_document_reject_description,
                buttonTextResource = string.understood,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(SignDocumentProcessViewModel.UIEvent.OnCloseClick) },
                onButtonClick = { viewModel.onUIEvent(SignDocumentProcessViewModel.UIEvent.OnCloseClick) }
            )
        }
        EVICERTIA_REJECTED_SECOND_TIME_STEP.value -> {
            AlertResult(
                iconResource = drawable.ic_alert,
                titleResource = string.sign_credit_max_attempts_title,
                descriptionResource = if (viewModel.idBrand == Brand.Guatemala.id) string.sign_credit_max_attempts_message_gt else string.sign_credit_max_attempts_message,
                buttonTextResource = string.understood,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(SignDocumentProcessViewModel.UIEvent.OnCloseClick) },
                onButtonClick = { viewModel.onUIEvent(SignDocumentProcessViewModel.UIEvent.OnCloseClick) }
            )
        }
        VALIDATE_IDENTITY.value -> {
            ValidateIdentityScreen(viewModel = viewModel)
        }
        ONFIDO_REJECTED_FIRST_TIME.value -> {
            AlertResult(
                iconResource = drawable.ic_error_symbol,
                titleResource = string.onfido_rejected_first_time_title,
                descriptionResource = if (viewModel.idBrand == Brand.Guatemala.id) string.onfido_rejected_first_time_subtitle_gt else string.onfido_rejected_second_time_title,
                buttonTextResource = string.onfido_rejected_action,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
                onButtonClick = { viewModel.onUIEvent(OnNavigateToHome) }
            )
        }
        ONFIDO_REJECTED_SECOND_TIME.value -> {
            AlertResult(
                iconResource = drawable.ic_error_symbol,
                titleResource = string.onfido_rejected_second_time_title,
                descriptionResource = if (viewModel.idBrand == Brand.Guatemala.id) string.onfido_rejected_second_time_subtitle_gt else string.onfido_rejected_second_time_subtitle,
                buttonTextResource = string.onfido_rejected_action,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
                onButtonClick = { viewModel.onUIEvent(OnNavigateToHome) }
            )
        }
        CONTINUE_VALIDATING_IDENTITY.value -> {
            AlertResult(
                iconResource = drawable.ic_onfido_continue,
                titleResource = string.continue_validating_identity_title,
                descriptionResource = string.continue_validating_identity_subtitle,
                buttonTextResource = string.understood,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
                onButtonClick = { viewModel.onUIEvent(OnNavigateToHome) }
            )
        }
        PROCESSING_THE_TRANSACTION.value -> {
            ProcessingTransactionScreen(viewModel = viewModel)
        }
    }

    // this is require to block the onBack event
    BackHandler(onBack = {})
}
