package com.multimoney.multimoney.presentation.ui.credit.signdocument

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_DOCUMENT_LINK
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.MmWebView
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SignDocumentScreen(
    navBackStackEntry: NavBackStackEntry,
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignDocumentViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.createDialog()
    }

    viewModel.onUIEvent(
        SignDocumentViewModel.UIEvent.OnInitializeText(
            stringResource(id = string.sign_credit_dialog_description)
        )
    )

    MmWebView(
        navBackStackEntry.arguments?.getString(SIGN_DOCUMENT_LINK) ?: "",
        LocalContext.current
    )
    if (viewModel.uiState.dialogParameters.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.dialogParameters.title),
            message = viewModel.uiState.dialogParameters.description,
            positiveButtonText = stringResource(id = viewModel.uiState.dialogParameters.positiveText),
            negativeButtonText = stringResource(id = viewModel.uiState.dialogParameters.negativeText),
            openDialogCustom = viewModel.uiState.dialogParameters.isActive,
            onPositiveAction = viewModel.uiState.dialogParameters.positiveAction
        )
    }
}