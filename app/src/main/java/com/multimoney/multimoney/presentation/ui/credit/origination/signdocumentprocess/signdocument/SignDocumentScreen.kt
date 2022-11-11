package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.signdocument

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel.UIEvent.OnInitializeText
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
