package com.multimoney.multimoney.presentation.ui.credit.origination.signdocument

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocument.SignDocumentViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocument.SignDocumentViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocument.SignDocumentViewModel.UIEvent.OnRejectClick
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.MmWebView
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SignDocumentScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignDocumentViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.apply {
            executeNavigation(onPopAndNavigate = onPopAndNavigate)
            createDialog()
        }
    }

    viewModel.onUIEvent(
        OnInitializeText(
            stringResource(id = string.sign_credit_dialog_description)
        )
    )
    SignDocumentContent(viewModel)
}

@Composable
@Preview
fun SignDocumentContent(viewModel: SignDocumentViewModel = hiltViewModel()) {
    if (viewModel.uiState.isAlertResultVisible) {
        AlertResult(
            iconResource = R.drawable.ic_alert,
            titleResource = R.string.sign_document_reject_title,
            descriptionResource = R.string.sign_document_reject_description,
            buttonTextResource = R.string.understood,
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onUIEvent(OnCloseClick) },
            onButtonClick = { viewModel.onUIEvent(OnCloseClick) }
        )
    } else {
        MmWebView(
            viewModel.uiState.signDocumentLink,
            LocalContext.current
        )

        // TODO: Remove this button when all functionalities are implemented

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
