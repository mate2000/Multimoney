package com.multimoney.multimoney.presentation.ui.smart.payment.sending

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun SendingAmountScreen(
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopBackStack: (NavEvent.PopBackStack) -> Unit = {},
    viewModel: SmartSelectSendingTypeViewModel = hiltViewModel()
){

}

@Composable
fun SendingAmountContent(viewModel: SmartSelectSendingTypeViewModel = hiltViewModel()){

}