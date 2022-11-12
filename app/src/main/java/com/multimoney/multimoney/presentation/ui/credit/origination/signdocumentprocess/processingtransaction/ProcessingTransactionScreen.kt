package com.multimoney.multimoney.presentation.ui.credit.origination.signdocumentprocess.processingtransaction

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun ProcessingTransactionScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: ProcessingTransactionViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }
    Column {
        Text(text = "Processing Transaction Screen")
    }
}
