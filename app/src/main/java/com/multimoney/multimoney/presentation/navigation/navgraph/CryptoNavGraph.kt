package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.multimoney.domain.model.accountsmart.toSmartAccountSmall
import com.multimoney.multimoney.presentation.navigation.CARD_STATUS
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ROUTE
import com.multimoney.multimoney.presentation.navigation.GLOBAL_CRYPTO_BALANCE
import com.multimoney.multimoney.presentation.navigation.HOME_STATE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.ID_TRANSACTION
import com.multimoney.multimoney.presentation.navigation.MOVEMENT_MONTH_LIMIT_EXCEEDED
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.STATUS_CREDIT
import com.multimoney.multimoney.presentation.navigation.STATUS_CRYPTO
import com.multimoney.multimoney.presentation.navigation.STATUS_SMART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.crypto.CryptoBalanceNavType
import com.multimoney.multimoney.presentation.navigation.navtype.crypto.MarketCryptoNavType
import com.multimoney.multimoney.presentation.ui.crypto.market.MarketScreen
import com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails.MarketCurrencyDetailsScreen
import com.multimoney.multimoney.presentation.ui.crypto.movements.CryptoCurrencyDetailsAllMovementsScreen
import com.multimoney.multimoney.presentation.ui.crypto.purchase.PurchaseCryptoFlow
import com.multimoney.multimoney.presentation.ui.crypto.receive.CryptoReceiveFlowScreen
import com.multimoney.multimoney.presentation.ui.crypto.sell.SellCryptoFlow
import com.multimoney.multimoney.presentation.ui.crypto.transferin.AmountExceededFormScreen
import com.multimoney.multimoney.presentation.ui.crypto.wallet.HomeWallet
import com.multimoney.multimoney.presentation.ui.crypto.wallet.currencydetail.WalletCurrencyDetailsScreen
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.CryptoHomeAllMovementsScreen
import com.multimoney.multimoney.presentation.ui.home.product.crypto.uisections.MaintenanceAlertScreen

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
                navArgument(PREVIOUS_SCREEN) { type = NavType.StringType },
                navArgument(ITEM_CRYPTO_MARKET) {
                    nullable = true
                    defaultValue = null
                    type = MarketCryptoNavType()
                }
            )
        ) { backStackEntry ->
            val parent = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.HomeScreen.route)
            }
            val viewModel = hiltViewModel<HomeViewModel>(parent)
            val accountsSmart =
                viewModel.uiState.balance?.balanceAccountSmart?.map { account ->
                    account.toSmartAccountSmall()
                } ?: emptyList()
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
                },
                smartAccounts = accountsSmart
            )
        }
        cryptoSendNavGraph(navController)
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
                navArgument(CARD_STATUS) { type = NavType.IntType },
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
            route = Screen.CryptoWalletDetailsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(IDENTIFICATION) { type = NavType.StringType },
                navArgument(ITEM_CRYPTO_CURRENCY) {
                    type = CryptoBalanceNavType()
                },
            ),
        ) {
            WalletCurrencyDetailsScreen(
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
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
            )
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
        // home view all crypto movements screen
        composable(
            route = Screen.CryptoHomeAllMovementsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType }
            )
        ) {
            CryptoHomeAllMovementsScreen(
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
        // crypto currency details all movements screen
        composable(
            route = Screen.CryptoCurrencyDetailsAllMovementsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(CRYPTO_ASSET) {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                }
            )
        ) {
            CryptoCurrencyDetailsAllMovementsScreen(
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
                    type = MarketCryptoNavType()
                },
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
        composable(
            route = Screen.CryptoSellFlow.route,
            arguments = listOf(
                navArgument(ITEM_CRYPTO_MARKET) {
                    nullable = true
                    defaultValue = null
                    type = MarketCryptoNavType()
                },
            )
        ) { backStackEntry ->
            val parent = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.HomeScreen.route)
            }
            val viewModel = hiltViewModel<HomeViewModel>(parent)
            val accountsSmart = viewModel.uiState.balance?.balanceAccountSmart?.map { account ->
                account.toSmartAccountSmall()
            } ?: emptyList()
            val balances = viewModel.uiState.balance?.balanceCryptoAccount?.items ?: emptyList()
            SellCryptoFlow(
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
                },
                accountsSmart = accountsSmart,
                cryptoBalances = balances
            )
        }
        composable(route = Screen.MaintenanceAlertScreen.route) {
            MaintenanceAlertScreen(
                onBackToHomeAction = {
                    navController.navigate(Screen.HomeScreen.route)
                }
            )
        }
        composable(
            route = Screen.ReleaseTransactionScreen.route,
            arguments = listOf(
                navArgument(CRYPTO_ASSET) {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                },
                navArgument(ID_TRANSACTION) {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                },
                navArgument(MOVEMENT_MONTH_LIMIT_EXCEEDED) {
                    nullable = false
                    defaultValue = false
                    type = NavType.BoolType
                },
                navArgument(PREVIOUS_SCREEN) {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.HomeScreen.route)
            }
            val viewModel = hiltViewModel<HomeViewModel>(parentEntry)
            val amountLimitExceeded = backStackEntry.arguments?.getBoolean(MOVEMENT_MONTH_LIMIT_EXCEEDED) ?: false

            AmountExceededFormScreen(
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
                sharedViewModel = viewModel,
                amountExceeded = amountLimitExceeded
            )
        }
    }

    composable(
        route = Screen.CryptoReceiveFlowScreen.route,
        listOf(
            navArgument(USER) { type = NavType.StringType },
            navArgument(ID_BRAND) { type = NavType.IntType },
            navArgument(ITEM_CRYPTO_MARKET) {
                nullable = true
                defaultValue = null
                type = MarketCryptoNavType()
            }
        )
    ) {
        CryptoReceiveFlowScreen(
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
}
