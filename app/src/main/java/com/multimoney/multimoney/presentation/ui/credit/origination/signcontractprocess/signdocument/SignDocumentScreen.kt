package com.multimoney.multimoney.presentation.ui.credit.origination.signcontractprocess.signdocument

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.credit.origination.signcontractprocess.SignDocumentProcessViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.signcontractprocess.SignDocumentProcessViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.MmWebView

@Composable
fun SignDocumentScreen(
    viewModel: SignDocumentProcessViewModel
) {
    viewModel.onUIEvent(
        OnInitializeText(
            stringResource(id = string.sign_credit_dialog_description)
        )
    )
    SignDocumentContent(viewModel)
}

@Composable
fun SignDocumentContent(viewModel: SignDocumentProcessViewModel = hiltViewModel()) {
    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            iconResource = R.drawable.ic_alert,
            titleResource = R.string.sign_document_reject_title,
            descriptionResource = R.string.sign_document_reject_description,
            buttonTextResource = R.string.understood,
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onUIEvent(SignDocumentProcessViewModel.UIEvent.OnCloseClick) },
            onButtonClick = { viewModel.onUIEvent(SignDocumentProcessViewModel.UIEvent.OnCloseClick) }
        )
    } else {
        MmWebView(
            viewModel.uiState.signDocumentUrl,
            LocalContext.current
        )

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
    }
}
