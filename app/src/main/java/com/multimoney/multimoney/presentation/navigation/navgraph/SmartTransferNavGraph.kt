package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.SMART_TRANSFER_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SmartAccountIDNavType
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.sending.SmartSelectSendingTypeScreen

fun NavGraphBuilder.smartTransferNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SmartSelectSendingTypeScreen.route,
        route = SMART_TRANSFER_ROUTE
    ) {
        composable(
            route = Screen.SmartSelectSendingTypeScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(SMART_IDS) { type = SmartAccountIDNavType() }
            )
        ) {
            SmartSelectSendingTypeScreen(
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
            route = Screen.SmartTransferIbanAccountScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.StringType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.StringType
                },
                navArgument(ID_LOAN_CLIENT) {
                    type = NavType.StringType
                }
            )
        ) {
            SmartTransferIbanScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(
            Screen.SmartTransferAmountScreen.route,
            arguments = listOf(
                navArgument(SMART_IDS) { type = SmartAccountIDNavType() }
            )
        ) {
            SmartTransferAmountScreen(
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
    }
}