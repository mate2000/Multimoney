package com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.multimoney.presentation.ui.crypto.movements.CryptoMovementsScreenContent
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoHomeAllMovementsViewModel.UIEvent.GetCryptoMovements
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoHomeAllMovementsViewModel.UIEvent.OnGetUserInfo
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoHomeAllMovementsViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoHomeAllMovementsViewModel.UIEvent.OnNavigateToReleaseTransaction

@Composable
fun CryptoHomeAllMovementsScreen(
    cryptoMovementsViewModel: CryptoHomeAllMovementsViewModel = hiltViewModel(),
    onPopBackStack: ((NavEvent.PopBackStack)) -> Unit = {},
    onNavigate: (NavEvent.Navigate) -> Unit = {},
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit = {}
) {

    LaunchedEffect(true) {
        cryptoMovementsViewModel.executeNavigation(
            onPopBackStack = onPopBackStack,
            onNavigate = onNavigate,
            onPopAndNavigate = onPopAndNavigate
        )

        cryptoMovementsViewModel.onUIEvent(OnGetUserInfo)
        cryptoMovementsViewModel.onUIEvent(GetCryptoMovements)
    }

    BackHandler {
        cryptoMovementsViewModel.onUIEvent(OnNavigateBack)
    }
    CryptoMovementsScreenContent(
        cryptoMovements = cryptoMovementsViewModel.uiState.cryptoMovements,
        onBackPressed = { cryptoMovementsViewModel.onUIEvent(OnNavigateBack) },
        onNavigateToReleaseTransaction = {cryptoMovementsViewModel.onUIEvent(OnNavigateToReleaseTransaction(it))}
    )
}
