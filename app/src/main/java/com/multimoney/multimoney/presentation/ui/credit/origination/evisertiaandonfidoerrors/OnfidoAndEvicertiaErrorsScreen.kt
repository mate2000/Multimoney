package com.multimoney.multimoney.presentation.ui.credit.origination.evisertiaandonfidoerrors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand.CostaRica
import com.multimoney.data.util.catalog.Brand.ElSalvador
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.credit.origination.evisertiaandonfidoerrors.OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.credit.origination.evisertiaandonfidoerrors.OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToOnfidoProcess
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.EVICERTIA_REJECTED_SECOND_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_FIRST_TIME
import com.multimoney.multimoney.presentation.util.catalog.OnfidoAndEvicertiaError.ONFIDO_REJECTED_SECOND_TIME

@Composable
fun OnfidoAndEvicertiaErrorsScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: OnfidoAndEvicertiaErrorsViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate, onPopBackStack = onPopBackStack)
    }

    OnfidoAndEvicertiaErrorsContent(
        viewModel.uiState.error,
        viewModel.idBrand,
        onNavigateHome = {
            viewModel.onUIEvent(OnNavigateToHome)
        },
        onNavigateToOnfido = {
            viewModel.onUIEvent(OnNavigateToOnfidoProcess)
        },
    )
}

@Preview
@Composable
fun OnfidoAndEvicertiaErrorsContent(
    error: String = EVICERTIA_REJECTED_FIRST_TIME.value,
    idBrand: Int = ElSalvador.id,
    onNavigateHome: () -> Unit = {},
    onNavigateToOnfido: () -> Unit = {},
) {
    when (error) {
        EVICERTIA_REJECTED_FIRST_TIME.value -> {
            AlertResult(
                iconResource = drawable.ic_alert,
                titleResource = string.sign_document_reject_title,
                descriptionResource = if (idBrand == CostaRica.id) string.sign_document_reject_description else string.sign_document_reject_description_sv,
                buttonTextResource = string.sign,
                isLeftButtonVisible = false,
                onRightButtonClick = { onNavigateHome() },
                onButtonClick = { onNavigateHome() },
            )
        }
        EVICERTIA_REJECTED_SECOND_TIME.value -> {
            AlertResult(
                iconResource = drawable.ic_alert,
                titleResource = string.sign_credit_max_attempts_title,
                descriptionResource = if (idBrand == CostaRica.id) string.sign_credit_max_attempts_message else string.sign_credit_max_attempts_message_sv,
                buttonTextResource = string.understood,
                isLeftButtonVisible = false,
                onRightButtonClick = { onNavigateHome() },
                onButtonClick = { onNavigateHome() },
            )
        }
        ONFIDO_REJECTED_FIRST_TIME.value -> {
            AlertResult(
                iconResource = drawable.ic_error_symbol,
                titleResource = string.onfido_rejected_first_time_title,
                descriptionResource = if (idBrand == CostaRica.id) string.onfido_rejected_first_time_subtitle else string.onfido_rejected_first_time_subtitle_sv,
                buttonTextResource = string.onfido_rejected_action,
                isLeftButtonVisible = false,
                onRightButtonClick = { onNavigateHome() },
                onButtonClick = { onNavigateToOnfido() },
            )
        }
        ONFIDO_REJECTED_SECOND_TIME.value -> {
            AlertResult(
                iconResource = drawable.ic_error_symbol,
                titleResource = string.onfido_rejected_second_time_title,
                descriptionResource = string.onfido_rejected_second_time_subtitle,
                buttonTextResource = string.onfido_rejected_action,
                isLeftButtonVisible = false,
                onRightButtonClick = { onNavigateHome() },
                onButtonClick = { onNavigateHome() },
            )
        }
    }
}
