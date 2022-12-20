package com.multimoney.multimoney.presentation.ui.smart.origination.sign

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignViewModel.UIEvent.OnInitializeText
import com.multimoney.multimoney.presentation.uielement.MmWebView

@Composable
fun SmartSignDocumentScreen(
    viewModel: SmartSignViewModel
) {
    viewModel.onUIEvent(
        OnInitializeText(
            stringResource(id = string.sign_credit_dialog_description)
        )
    )
    MmWebView(
        viewModel.uiState.signDocumentUrl,
        LocalContext.current
    )
}