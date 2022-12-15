package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ROUTE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWallet

fun NavGraphBuilder.cryptoNavGraph(
    navController: NavHostController
) {

    navigation(
        startDestination = Screen.CryptoWalletScreen.route,
        route = CRYPTO_ROUTE
    ) {

        composable(
            route = Screen.CryptoWalletScreen.route,
            arguments = listOf(navArgument(ID_BRAND) { type = NavType.IntType }),
        ) {
            HomeWallet() {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            }
        }
    }
}