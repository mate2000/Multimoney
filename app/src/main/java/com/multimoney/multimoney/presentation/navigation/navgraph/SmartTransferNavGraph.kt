package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.IBAN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.SMART_TRANSFER_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SinpeAccountNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SmartAccountIDNavType
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.register.SmartTransferRegisterIbanScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeScreen

fun NavGraphBuilder.smartTransferNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SmartSelectSendingTypeScreen.route,
        route = SMART_TRANSFER_ROUTE
    ) {
        composable(
            route = Screen.SmartSelectSendingTypeScreen.route,
            arguments = listOf(
                navArgument(USER) { type = NavType.StringType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(IDENTIFICATION) { type = NavType.StringType },
                navArgument(SMART_IDS) { type = SmartAccountIDNavType() },
                navArgument(ID_CLIENT) { type = NavType.IntType },
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
                navArgument(SMART_IDS) {
                    type = SmartAccountIDNavType()
                },
                navArgument(USER) {
                    type = NavType.StringType
                },
                navArgument(ID_BRAND) {
                    type = NavType.StringType
                },
                navArgument(IDENTIFICATION) {
                    type = NavType.StringType
                },
                navArgument(PREVIOUS_SCREEN) {
                    type = NavType.StringType
                },
                navArgument(ID_CLIENT) {
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
                navArgument(SMART_IDS) { type = SmartAccountIDNavType() },
                navArgument(IBAN_ACCOUNT) { type = SinpeAccountNavType() },
                navArgument(PREVIOUS_SCREEN) { type = NavType.StringType }
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

        composable(
            route = Screen.SmartTransferRegisterIbanScreen.route,
            arguments = listOf(
                navArgument(ID_CLIENT) { type = NavType.StringType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(IDENTIFICATION) { type = NavType.StringType },
                navArgument(PREVIOUS_SCREEN) { type = NavType.StringType },
                navArgument(ID_CLIENT) { type = NavType.IntType }
            )
        ) {
            SmartTransferRegisterIbanScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(PREVIOUS_IS_RESTART,
                        it.isRestart)
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
