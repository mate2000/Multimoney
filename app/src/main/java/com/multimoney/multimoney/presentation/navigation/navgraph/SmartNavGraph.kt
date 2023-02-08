package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.HOME_STATE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.SMART_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsScreen
import com.multimoney.multimoney.presentation.ui.smart.SmartScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.evicertiaandonfidoerrors.OnfidoAndEvicertiaErrorsScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.continuevalidatingonfido.ContinueValidatingOnfidoScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.onfidoscenarios.onfidoapproved.ApprovedByOnfidoScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.sign.SmartSignScreen

const val ACCOUNT_TOKEN = "account_token"
const val SMART_PAYMENT_ACCOUNTS = "smart_payment_accounts"
const val ID_GLOBAL_REQUEST = "id_global_request"

fun NavGraphBuilder.smartNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SmartScreen.route,
        route = SMART_ROUTE
    ) {
        composable(
            Screen.SmartScreen.route,
            arguments = listOf(
                navArgument(ID_GLOBAL_REQUEST) { type = NavType.LongType }
            )
        ) {
            SmartScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
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
            Screen.SmartOnfidoScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(SIGN_DOCUMENT_ID_PRINT) { type = NavType.LongType },
                navArgument(SIGN_DOCUMENT_GLOBAL_ID) { type = NavType.LongType }
            )
        ) {
            SmartOnfidoScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
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
            Screen.SmartMovementsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                }
            )
        ) {
            SmartMovementsScreen(
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
            route = Screen.SmartSignScreen.route,
            arguments = listOf(
                navArgument(SIGN_DOCUMENT_ID_PRINT) { type = NavType.LongType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ID_USER_REQUEST) { type = NavType.LongType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(IS_SMART_EVICERTIA) { type = NavType.BoolType },
                navArgument(SIGN_DOCUMENT_GLOBAL_ID) { type = NavType.LongType }
            )
        ) {
            SmartSignScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
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
            route = Screen.SmartOnfidoAndEvicertiaErrorsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(ID_USER_REQUEST) { type = NavType.LongType }
            )
        ) {
            OnfidoAndEvicertiaErrorsScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
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
            route = Screen.ApprovedByOnfidoScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType }
            )
        ) {
            ApprovedByOnfidoScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
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
            route = Screen.SmartContinueValidatingOnfidoScreen.route
        ) {
            ContinueValidatingOnfidoScreen(
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
