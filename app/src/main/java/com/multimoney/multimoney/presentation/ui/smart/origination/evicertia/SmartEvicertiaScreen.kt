package com.multimoney.multimoney.presentation.ui.smart.origination.evicertia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.MmWebViewHtml

@Composable
fun SmartEvicertiaScreen(viewModel: SmartEvicertiaViewModel = hiltViewModel()) {
    Column(modifier = Modifier.fillMaxSize()) {
        MmWebViewHtml(
            viewModel.uiState.html,
            LocalContext.current
        )

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