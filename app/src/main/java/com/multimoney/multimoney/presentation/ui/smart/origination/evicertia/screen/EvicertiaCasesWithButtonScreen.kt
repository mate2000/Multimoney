package com.multimoney.multimoney.presentation.ui.smart.origination.evicertia.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.Brand.CostaRica
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.ui.credit.origination.evisertiaandonfidoerrors.OnfidoAndEvicertiaErrorsViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.evisertiaandonfidoerrors.OnfidoAndEvicertiaErrorsViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun EvicertiaCasesWithButtonScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: OnfidoAndEvicertiaErrorsViewModel = hiltViewModel()
) {
    AlertResult(
        iconResource = drawable.ic_alert,
        titleResource = string.sign_document_reject_title,
        descriptionResource = if (viewModel.idBrand == CostaRica.id) string.sign_document_reject_description else string.sign_document_reject_description_sv,
        buttonTextResource = string.understood,
        isLeftButtonVisible = false,
        onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
        onButtonClick = { viewModel.onUIEvent(OnNavigateToHome) }
    )
}