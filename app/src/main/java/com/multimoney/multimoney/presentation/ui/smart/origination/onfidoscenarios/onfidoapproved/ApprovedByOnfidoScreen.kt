package com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnMakeFirstPayment
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomTextButton
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun ApprovedByOnfidoScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: ApprovedByOnfidoViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    AlertResult(
        iconResource = R.drawable.ic_success_symbol,
        titleResource = R.string.approved_by_onfido_title,
        buttonTextResource = R.string.approved_by_onfido_buttton_text,
        isLeftButtonVisible = false,
        onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
        onButtonClick = { viewModel.onUIEvent(OnMakeFirstPayment) }
    )

    Column {
        CustomTextButton(
            textResource = R.string.finalize,
            onClick = {viewModel.onUIEvent(OnNavigateToHome)}
        )
    }
}
