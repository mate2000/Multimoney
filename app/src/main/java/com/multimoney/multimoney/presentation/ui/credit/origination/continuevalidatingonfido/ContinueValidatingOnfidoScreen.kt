package com.multimoney.multimoney.presentation.ui.credit.origination.continuevalidatingonfido

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.credit.origination.continuevalidatingonfido.ContinueValidatingOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun ContinueValidatingOnfidoScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: ContinueValidatingOnfidoViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack)
    }

    ContinueValidatingOnfidoContent {
        viewModel.onUIEvent(OnNavigateToHome)
    }
}

@Preview
@Composable
fun ContinueValidatingOnfidoContent(navigateToHome: () -> Unit = {}) {
    AlertResult(
        iconResource = drawable.ic_success_symbol,
        titleResource = string.continue_validating_identity_title,
        descriptionResource = string.continue_validating_identity_subtitle,
        buttonTextResource = string.understood,
        isLeftButtonVisible = false,
        onRightButtonClick = { navigateToHome() },
        onButtonClick = { navigateToHome() }
    )
}
