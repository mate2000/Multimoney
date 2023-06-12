package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.signdocument

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.SignDocumentProcessViewModel
import com.multimoney.multimoney.presentation.uielement.MmWebView

@Composable
fun SignDocumentScreen(
    viewModel: SignDocumentProcessViewModel
) {
    MmWebView(
        viewModel.uiState.signDocumentUrl,
        LocalContext.current
    )
}
