package com.multimoney.multimoney.presentation.ui.crypto.movements

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun CryptoMovementsScreen(
    cryptoMovementsViewModel: CryptoMovementsScreenViewModel = hiltViewModel(),
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {}
) {

}

@Composable
fun CryptoMovementsScreenContent() {

}