package com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.continuevalidatingonfido

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.continuevalidatingonfido.ContinueValidatingOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun ContinueValidatingOnfidoScreen(
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: ContinueValidatingOnfidoViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.executeNavigation(onPopBackStack = onPopBackStack)
    }

    AlertResult(
        iconResource = R.drawable.ic_logo_multimoney,
        iconModifier = Modifier.size(48.dp),
        titleResource = R.string.smart_continue_validating_identity_title,
        descriptionResource = R.string.smart_continue_validating_identity_subtitle,
        buttonTextResource = R.string.understood,
        isLeftButtonVisible = false,
        onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
        onButtonClick = { viewModel.onUIEvent(OnNavigateToHome) }
    )
}
