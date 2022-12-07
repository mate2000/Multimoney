package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.SMART_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsScreen
import com.multimoney.multimoney.presentation.ui.smart.SmartScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferScreen

const val ACCOUNT_TOKEN = "account_token"

fun NavGraphBuilder.smartNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SmartScreen.route,
        route = SMART_ROUTE
    ) {
        composable(
            Screen.SmartScreen.route
        ) {
            SmartScreen(onNavigate = {
                navController.navigate(it.route)
            }, onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }

        composable(
            Screen.SmartOnfidoScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(SIGN_DOCUMENT_ID_PRINT) { type = NavType.LongType }
            )
        ) {
            SmartOnfidoScreen(onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }
        composable(Screen.SavingMethodTransferScreen.route) {
            SavingMethodTransferScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
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
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Screen.SmartPaymentScreen.route
        ) {
            SmartPaymentMethodScreen(onNavigate = {
                navController.navigate(it.route)
            }, onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }
    }

    composable(
        route = Screen.SmartPaymentAccountScreen.route
    ) {
        SmartPaymentAccountScreen(onNavigate = {
            navController.navigate(it.route)
        }, onPopAndNavigate = {
            navController.navigate(it.route) {
                popUpTo(it.popTo) { inclusive = true }
            }
        })
    }
}
