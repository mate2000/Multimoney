package com.multimoney.multimoney.presentation.ui.credit.signdocuement

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SignDocumentScreen(
    navBackStackEntry: NavBackStackEntry,
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SignDocumentViewModel = hiltViewModel()
) {

}