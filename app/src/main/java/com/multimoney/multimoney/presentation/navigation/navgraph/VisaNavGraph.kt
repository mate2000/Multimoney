package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.HOME_STATE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.VISA_ROUTE
import com.multimoney.multimoney.presentation.navigation.navtype.home.BalanceCardInformationNavType
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardScreen
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingScreen
import com.multimoney.multimoney.presentation.ui.visa.preferences.VisaPreferencesScreen
import com.multimoney.multimoney.presentation.ui.visa.verification.VisaVerifyInformationScreen
import com.multimoney.multimoney.presentation.ui.visa.verifydeposit.VisaVerifyDepositScreen

const val BALANCE_CARD_INFORMATION = "balance_card_information"
const val AVAILABLE_BALANCE_LABEL = "available_balance_label"
const val ID_CARD = "id_card"
const val CALLER_ID = "caller_id"
const val ACCOUNT_TOKEN_CREDIT = "account_token_credit"
const val VISA_DIRECT_USER = "visa_direct_user"

fun NavGraphBuilder.visaNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.VisaIssuanceScreen.route,
        route = VISA_ROUTE
    ) {
        composable(
            route = Screen.VisaIssuanceScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(ID_CLIENT) { type = NavType.IntType },
                navArgument(ID_LOAN_CLIENT) { type = NavType.IntType },
                navArgument(BALANCE_CARD_INFORMATION) { type = BalanceCardInformationNavType() }
            )
        ) { navBackStackEntry ->
            VisaIssuanceScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.VisaCardScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(ID_CLIENT) { type = NavType.IntType },
                navArgument(ID_LOAN_CLIENT) { type = NavType.IntType },
                navArgument(BALANCE_CARD_INFORMATION) { type = BalanceCardInformationNavType() }
            )
        ) { navBackStackEntry ->
            VisaCardScreen(
                isRestart = navController.currentBackStackEntry?.savedStateHandle?.get(PREVIOUS_IS_RESTART) ?: true,
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.VisaTokenizationWaitingScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(BALANCE_CARD_INFORMATION) { type = BalanceCardInformationNavType() }
            )
        ) {
            VisaTokenizationWaitingScreen(
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                }
            )
        }
        composable(
            route = Screen.VisaPreferencesScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(BALANCE_CARD_INFORMATION) { type = BalanceCardInformationNavType() },
                navArgument(ID_CLIENT) { type = NavType.IntType },
                navArgument(ID_LOAN_CLIENT) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.HomeScreen.route)
            }
            val viewModel = hiltViewModel<HomeViewModel>(parentEntry)
            VisaPreferencesScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                sharedViewModel = viewModel
            )
        }
        composable(
            route = Screen.VisaVerifyDepositScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType }
            )
        ) {
            VisaVerifyDepositScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                }
            )
        }
        composable(
            route = Screen.VisaVerifyInformationScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType }
            )
        ) {
            VisaVerifyInformationScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                }
            )
        }
    }
}
