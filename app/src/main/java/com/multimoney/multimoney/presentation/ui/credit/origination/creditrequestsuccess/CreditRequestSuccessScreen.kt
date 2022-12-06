package com.multimoney.multimoney.presentation.ui.credit.origination.creditrequestsuccess

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.ui.credit.origination.creditrequestsuccess.CreditRequestSuccessViewModel.UIEvent.OnCloseClick
import com.multimoney.multimoney.presentation.ui.credit.origination.creditrequestsuccess.CreditRequestSuccessViewModel.UIEvent.OnUnderstoodClick
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun CreditRequestSuccessScreen(
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {},
    viewModel: CreditRequestSuccessViewModel = hiltViewModel()
) {
    // Properties

    val focusManager = LocalFocusManager.current

    // Navigation

    LaunchedEffect(true) {
        viewModel.executeNavigation(onPopAndNavigate = onPopAndNavigate)
    }

    // View

    AlertResult(
        iconResource = R.drawable.ic_success_symbol,
        titleResource = R.string.credit_request_sent_successfully,
        descriptionResource = R.string.credit_request_info_verification_wait,
        buttonTextResource = R.string.understood,
        isTopNavBarVisible = true,
        isRightButtonVisible = true,
        isLeftButtonVisible = false,
        onRightButtonClick = { viewModel.onUIEvent(OnCloseClick(focusManager)) },
        onButtonClick = { viewModel.onUIEvent(OnUnderstoodClick) }
    )
}