package com.multimoney.multimoney.presentation.ui.onboarding.signdocs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.MmWebView

@Composable
fun SignDocumentsScreen(
    siteUrl: String,
    viewModel: SignViewModel = hiltViewModel()
) {

    LaunchedEffect(true) {
        viewModel.createDialog()
    }

    viewModel.onUIEvent(
        SignViewModel.UIEvent.OnInitializeText(
            stringResource(id = R.string.sign_credit_dialog_description)
        )
    )

    MmWebView(
        ,
        LocalContext.current
    )
    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.title),
            message = viewModel.uiState.openDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveText),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeText),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}