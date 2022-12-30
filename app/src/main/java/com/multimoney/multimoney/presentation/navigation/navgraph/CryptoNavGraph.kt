package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ROUTE
import com.multimoney.multimoney.presentation.navigation.GLOBAL_CRYPTO_BALANCE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.crypto.CryptoCurrencyNavType
import com.multimoney.multimoney.presentation.ui.crypto.currencydetail.CurrencyDetailScreen
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWallet

const val ITEM_CRYPTO_CURRENCY = "item_crypto_currency"

fun NavGraphBuilder.cryptoNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.CryptoWalletScreen.route,
        route = CRYPTO_ROUTE
    ) {
        composable(
            route = Screen.CryptoWalletScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(GLOBAL_CRYPTO_BALANCE) { type = NavType.FloatType },
            ),
        ) {
            HomeWallet(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onNavigate = { navController.navigate(it.route) },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                },
            )
        }
        composable(
            route = Screen.CryptoCurrencyMovementsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(IDENTIFICATION) { type = NavType.StringType },
                navArgument(ITEM_CRYPTO_CURRENCY) {
                    type = CryptoCurrencyNavType()
                },
            ),
        ) {
            CurrencyDetailScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        PREVIOUS_IS_RESTART,
                        it.isRestart
                    )
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onNavigate = { navController.navigate(it.route) },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
    }
}