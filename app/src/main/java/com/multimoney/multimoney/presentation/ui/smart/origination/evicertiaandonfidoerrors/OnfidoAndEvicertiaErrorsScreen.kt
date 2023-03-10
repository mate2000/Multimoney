package com.multimoney.multimoney.presentation.ui.smart.origination.evicertiaandonfidoerrors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.smart.origination.evicertiaandonfidoerrors.OnfidoAndEvicertiaErrorsViewModel.UIEvent
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_SECOND_TIME

@Composable
fun OnfidoAndEvicertiaErrorsScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: OnfidoAndEvicertiaErrorsViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate, onPopBackStack = onPopBackStack)
    }

    when (viewModel.uiState.error) {
        OnfidoAndEvicertiaError.ONFIDO_REJECTED_FIRST_TIME.value -> {
            AlertResult(
                iconResource = R.drawable.ic_error_symbol,
                titleResource = R.string.rejected_by_onfido_title,
                descriptionResource = R.string.smart_rejected_by_onfido_subtitle,
                buttonTextResource = R.string.error_button_try_again,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateToHome) },
                onButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateToOnfidoProcess) }
            )
        }
        OnfidoAndEvicertiaError.ONFIDO_REJECTED_SECOND_TIME.value -> {
            AlertResult(
                iconResource = R.drawable.ic_error_symbol,
                titleResource = R.string.smart_onfido_rejected_second_time_title,
                descriptionResource = R.string.smart_onfido_rejected_second_time_subtitle,
                buttonTextResource = R.string.smart_onfido_rejected_action_second_time,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateToHome) },
                onButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateToHome) }
            )
        }
        EVICERTIA_REJECTED_FIRST_TIME.value -> {
            AlertResult(
                iconResource = drawable.ic_alert,
                titleResource = string.smart_evicertia_rejected_title,
                descriptionResource = string.smart_evicertia_rejected_subtitle,
                buttonTextResource = string.understood,
                isLeftButtonVisible = false,
                onRightButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateToHome) },
                onButtonClick = { viewModel.onUIEvent(UIEvent.OnNavigateToHome) }
            )
        }
        EVICERTIA_REJECTED_SECOND_TIME.value -> {
            viewModel.onUIEvent(UIEvent.OnNavigateToHome)
        }
    }
}
