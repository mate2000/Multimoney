package com.multimoney.multimoney.presentation.ui.smart.origination.evicertiaandonfidoerrors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError

@Composable
fun OnfidoAndEvicertiaErrorsScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: OnfidoAndEvicertiaErrorsViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    when (viewModel.uiState.error) {
        OnfidoAndEvicertiaError.ONFIDO_REJECTED_FIRST_TIME.value -> {
            AlertResult(
                iconResource = R.drawable.ic_error_symbol,
                titleResource = R.string.rejected_by_onfido_title,
                descriptionResource = R.string.smart_rejected_by_onfido_subtitle,
                buttonTextResource = R.string.rejected_by_onfido_buttton_text,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToHome) },
                onButtonClick = { viewModel.onUIEvent(OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToOnfidoProcess) }
            )
        }
        OnfidoAndEvicertiaError.ONFIDO_REJECTED_SECOND_TIME.value -> {
            AlertResult(
                iconResource = R.drawable.ic_error_symbol,
                titleResource = R.string.smart_onfido_rejected_second_time_title,
                descriptionResource = R.string.smart_onfido_rejected_second_time_subtitle,
                buttonTextResource = R.string.smart_onfido_rejected_action_second_time,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToHome) },
                onButtonClick = { viewModel.onUIEvent(OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToHome) }
            )
        }
    }
}