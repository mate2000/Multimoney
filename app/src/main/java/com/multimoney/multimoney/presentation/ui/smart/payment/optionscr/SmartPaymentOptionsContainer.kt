package com.multimoney.multimoney.presentation.ui.smart.payment.optionscr

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.ui.smart.common.selectsmartaccount.BaseSelectSmartAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.common.selectsmartaccount.SmartPaymentOptionsScreen
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SmartPaymentOptionsContainer(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartPaymentOptionsViewModel = hiltViewModel()
) {
    SmartPaymentOptionsScreen(
        viewModel = viewModel,
        onPopBackStack = onPopBackStack,
        onNavigate = onNavigate,
        topNavBar = {
            TopNavBar(
                onRightButtonClick = { viewModel.onUIEvent(OnNavigateBack) },
                isLeftButtonVisible = false
            )
        }
    )
}