package com.multimoney.multimoney.presentation.ui.crypto.movements

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.ui.crypto.movements.CryptoCurrencyDetailsAllMovementsScreenViewModel.UIEvent.OnNavigateToReleaseTransaction
import com.multimoney.multimoney.presentation.util.NavEvent

@Composable
fun CryptoCurrencyDetailsAllMovementsScreen(
    cryptoMovementsViewModel: CryptoCurrencyDetailsAllMovementsScreenViewModel = hiltViewModel(),
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {}
) {
    LaunchedEffect(true) {
        cryptoMovementsViewModel.apply {
            executeNavigation(
                onPopBackStack = onPopBackStack,
                onNavigate = onNavigate,
                onPopAndNavigate = onPopAndNavigate
            )
            onUIEvent(CryptoCurrencyDetailsAllMovementsScreenViewModel.UIEvent.OnGetUserInfo)
            onUIEvent(CryptoCurrencyDetailsAllMovementsScreenViewModel.UIEvent.GetCryptoMovements)
        }
    }

    BackHandler {
        cryptoMovementsViewModel.onUIEvent(CryptoCurrencyDetailsAllMovementsScreenViewModel.UIEvent.OnNavigateBack)
    }
    CryptoMovementsScreenContent(
        cryptoMovements = cryptoMovementsViewModel.uiState.cryptoMovements,
        onBackPressed = {
            cryptoMovementsViewModel.onUIEvent(
                CryptoCurrencyDetailsAllMovementsScreenViewModel.UIEvent.OnNavigateBack
            )
        },
        onNavigateToReleaseTransaction = {
            cryptoMovementsViewModel.onUIEvent(
                OnNavigateToReleaseTransaction(it)
            )
        }
    )
}