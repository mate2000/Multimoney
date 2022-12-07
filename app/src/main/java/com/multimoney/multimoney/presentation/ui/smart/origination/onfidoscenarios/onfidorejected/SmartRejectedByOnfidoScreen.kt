package com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidorejected

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidorejected.SmartRejectedByOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidorejected.SmartRejectedByOnfidoViewModel.UIEvent.OnValidateIdentity
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SmartRejectedByOnfidoScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: SmartRejectedByOnfidoViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    AlertResult(
        iconResource = R.drawable.ic_error_symbol,
        titleResource = R.string.rejected_by_onfido_title,
        descriptionResource = R.string.smart_rejected_by_onfido_subtitle,
        buttonTextResource = R.string.rejected_by_onfido_buttton_text,
        isLeftButtonVisible = false,
        onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
        onButtonClick = { viewModel.onUIEvent(OnValidateIdentity) }
    )
}