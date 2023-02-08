package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.DESCRIPTION_CURRENCY
import com.multimoney.multimoney.presentation.navigation.HOME_STATE
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.QR_CODE_RESULT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.crypto.send.CryptoSendFlow
import com.multimoney.multimoney.presentation.ui.qrcodescanner.QrCodeScannerScreen

fun NavGraphBuilder.cryptoSendNavGraph(
    navController: NavHostController
) {
    composable(
        route = Screen.CryptoSendFlow.route,
        arguments = listOf(
            navArgument(CRYPTO_ASSET) {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            },
            navArgument(DESCRIPTION_CURRENCY) {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            }
        )
    ) {
        CryptoSendFlow(
            qrCodeResult = navController.currentBackStackEntry?.savedStateHandle?.get<String>(
                QR_CODE_RESULT
            ) ?: "",
            onNavigate = { navController.navigate(it.route) },
            onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            },
            onPopBackStack = {
                navController.getBackStackEntry(it.popTo).savedStateHandle.set(
                    PREVIOUS_IS_RESTART,
                    it.isRestart
                )
                navController.getBackStackEntry(it.popTo).savedStateHandle.set(
                    HOME_STATE,
                    it.homeState
                )
                navController.popBackStack(
                    route = it.popTo,
                    inclusive = false,
                    saveState = false
                )
            },
            onNavigateToQrCodeScanner = {
                navController.navigate(Screen.QrCodeScannerScreen.route)
            }
        )
    }
    composable(
        route = Screen.QrCodeScannerScreen.route,
    ) {
        QrCodeScannerScreen(onPopBackStack = {
            navController.previousBackStackEntry?.savedStateHandle?.set(QR_CODE_RESULT, it)
            navController.popBackStack()
        })
    }
}
