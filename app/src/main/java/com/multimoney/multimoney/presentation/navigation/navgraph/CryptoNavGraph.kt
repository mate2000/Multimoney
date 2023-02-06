package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.multimoney.multimoney.presentation.navigation.CARD_STATUS
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ROUTE
import com.multimoney.multimoney.presentation.navigation.DESCRIPTION_CURRENCY
import com.multimoney.multimoney.presentation.navigation.GLOBAL_CRYPTO_BALANCE
import com.multimoney.multimoney.presentation.navigation.HOME_STATE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.STATUS_CREDIT
import com.multimoney.multimoney.presentation.navigation.STATUS_CRYPTO
import com.multimoney.multimoney.presentation.navigation.STATUS_SMART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.crypto.CryptoBalanceNavType
import com.multimoney.multimoney.presentation.navigation.navtype.crypto.CryptoCoinNavType
import com.multimoney.multimoney.presentation.ui.crypto.wallet.currencydetail.CurrencyMovementsScreen
import com.multimoney.multimoney.presentation.ui.crypto.market.MarketScreen
import com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails.MarketCurrencyDetailsScreen
import com.multimoney.multimoney.presentation.ui.crypto.movements.CryptoMovementsAllScreen
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoFlow
import com.multimoney.multimoney.presentation.ui.crypto.send.CryptoSendFlow
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWallet

const val ITEM_CRYPTO_CURRENCY = "item_crypto_currency"
const val ITEM_CRYPTO_MARKET = "item_crypto_MARKET"

fun NavGraphBuilder.cryptoNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.CryptoWalletScreen.route,
        route = CRYPTO_ROUTE
    ) {
        composable(
            route = Screen.PurchaseCryptoFlow.route,
            arguments = listOf(
                navArgument(ITEM_CRYPTO_MARKET) {
                    nullable = true
                    defaultValue = null
                    type = CryptoCoinNavType()
                }
            )
        ) {
            PurchaseCryptoFlow(
                onNavigate = {
                    navController.navigate(it.route)
                },
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
                }
            )
        }
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
                }
            )
        }
        composable(
            route = Screen.CryptoWalletScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ID_CLIENT) { type = NavType.IntType },
                navArgument(GLOBAL_CRYPTO_BALANCE) { type = NavType.FloatType },
                navArgument(ID_LOAN_CLIENT) { type = NavType.IntType },
                navArgument(STATUS_CREDIT) { type = NavType.IntType },
                navArgument(STATUS_SMART) { type = NavType.IntType },
                navArgument(STATUS_CRYPTO) { type = NavType.IntType },
                navArgument(CARD_STATUS) { type = NavType.IntType }
            )
        ) {
            HomeWallet(
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
                onNavigate = { navController.navigate(it.route) },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.CryptoCurrencyMovementsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(IDENTIFICATION) { type = NavType.StringType },
                navArgument(ITEM_CRYPTO_CURRENCY) {
                    type = CryptoBalanceNavType()
                },
            ),
        ) {
            CurrencyMovementsScreen(
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
                onNavigate = { navController.navigate(it.route) },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.CryptoMarketScreen.route,
            arguments = listOf(navArgument(ID_BRAND) { type = NavType.IntType })
        ) {
            MarketScreen(
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
        composable(
            route = Screen.CryptoMovementsAllScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(CRYPTO_ASSET) {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                }
            )
        ) {
            CryptoMovementsAllScreen(
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
        composable(
            route = Screen.CryptoCurrencyDetailsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ITEM_CRYPTO_MARKET) {
                    nullable = true
                    defaultValue = null
                    type = CryptoCoinNavType()
                }
            )
        ) {
            MarketCurrencyDetailsScreen(
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
