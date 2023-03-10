package com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnCallQueryGetUserStatusInfo
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnFirsButtonClick
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnSecondButtonClick
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoViewModel.UIEvent.OnSetUpDialogData
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.LoadingIndicator
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun ApprovedByOnfidoScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    viewModel: ApprovedByOnfidoViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onPopAndNavigate = onPopAndNavigate,
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate
        )
        viewModel.onUIEvent(OnCallQueryGetUserStatusInfo)
        viewModel.onUIEvent(OnSetUpDialogData)
    }

    AlertResult(
        iconResource = R.drawable.ic_success_symbol,
        titleResource = viewModel.uiState.alertTitleResource,
        descriptionResource = viewModel.uiState.alertMessageResource,
        buttonTextResource = viewModel.uiState.alertButtonTextResource,
        isLeftButtonVisible = false,
        isRightButtonVisible = viewModel.comingFromCrypto,
        onRightButtonClick = { viewModel.onUIEvent(OnNavigateToHome) },
        onButtonClick = { viewModel.onUIEvent(OnFirsButtonClick) },
        isSecondaryButtonVisible = true,
        secondaryButtonTextResource = viewModel.uiState.alertSecondButtonTextResource,
        onSecondaryButtonClick = { viewModel.onUIEvent(OnSecondButtonClick) }
    )

    LoadingIndicator(viewModel.uiState.isLoading)

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = viewModel.uiState.openDialog.description,
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.openDialog.negativeResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction
        )
    }
}
