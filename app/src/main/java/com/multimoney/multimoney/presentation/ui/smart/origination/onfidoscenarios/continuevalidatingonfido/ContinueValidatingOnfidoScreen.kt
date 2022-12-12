package com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.continuevalidatingonfido

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.continuevalidatingonfido.ContinueValidatingOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun ContinueValidatingOnfidoScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
   viewModel: ContinueValidatingOnfidoViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    AlertResult(
        iconResource = R.drawable.ic_logo_multimoney,
        titleResource = R.string.smart_continue_validating_identity_title,
        descriptionResource = R.string.continue_validating_identity_subtitle,
        buttonTextResource = R.string.understood,
        isLeftButtonVisible = false,
        onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
        onButtonClick = { viewModel.onUIEvent(OnNavigateToHome) }
    )
}