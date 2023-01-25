package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.DESTINY_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.HOME_STATE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.ORIGIN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.SECOND_SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT_LIST
import com.multimoney.multimoney.presentation.navigation.SMART_TRANSFER_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TRANSFER_TYPE
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SinpeAccountNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SmartAccountIDListNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SmartAccountIDNavType
import com.multimoney.multimoney.presentation.ui.smart.transfer.addaccount.SmartAddAccountScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.amount.SmartTransferAmountScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.register.SmartTransferRegisterIbanScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.smartaccount.SelectSmartAccountContainer
import com.multimoney.multimoney.presentation.ui.smart.transfer.otherbanks.addaccount.SmartAddOtherBankAccountScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.sending.SmartSelectSendingTypeScreen
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.amount.OwnTransferAmountScreen

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
                navArgument(SMART_ACCOUNT) { type = SmartAccountIDNavType() },
                navArgument(SECOND_SMART_ACCOUNT) { type = SmartAccountIDNavType() },
                navArgument(ID_CLIENT) { type = NavType.IntType },
                navArgument(PREVIOUS_SCREEN) { type = NavType.StringType }
            )
        ) {
            SmartSelectSendingTypeScreen(
                onNavigate = {
                    navController.navigate(it.route)
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
            route = Screen.SmartTransferIbanAccountScreen.route,
            arguments = listOf(
                navArgument(SMART_ACCOUNT) {
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
            Screen.SmartTransferAmountScreen.route,
            arguments = listOf(
                navArgument(ORIGIN_ACCOUNT) { type = SmartAccountIDNavType() },
                navArgument(DESTINY_ACCOUNT) { type = SinpeAccountNavType() },
                navArgument(TRANSFER_TYPE) { type = NavType.IntType }
            )
        ) {
            SmartTransferAmountScreen(
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
            route = Screen.SmartSelectAccountScreen.route,
            arguments = listOf(
                navArgument(SMART_ACCOUNT_LIST) {
                    type = SmartAccountIDListNavType()
                },
                navArgument(USER) { type = NavType.StringType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ID_CLIENT) { type = NavType.StringType }
            )
        ) {
            SelectSmartAccountContainer(
                onNavigate = {
                    navController.navigate(it.route)
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
            route = Screen.SmartTransferRegisterIbanScreen.route,
            arguments = listOf(
                navArgument(ID_CLIENT) { type = NavType.StringType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(IDENTIFICATION) { type = NavType.StringType },
                navArgument(PREVIOUS_SCREEN) { type = NavType.StringType },
                navArgument(ID_CLIENT) { type = NavType.IntType },
                navArgument(SMART_ACCOUNT) { type = SmartAccountIDNavType() }
            )
        ) {
            SmartTransferRegisterIbanScreen(
                onNavigate = {
                    navController.navigate(it.route)
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
            Screen.OwnTransferAmountScreen.route,
            arguments = listOf(
                navArgument(ORIGIN_ACCOUNT) { type = SmartAccountIDNavType() },
                navArgument(DESTINY_ACCOUNT) { type = SmartAccountIDNavType() },
                navArgument(TRANSFER_TYPE) { type = NavType.IntType }
            )
        ) {
            OwnTransferAmountScreen(
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
            route = Screen.SmartAddSACAccountScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(SMART_ACCOUNT) { type = SmartAccountIDNavType() },
                navArgument(TRANSFER_TYPE) { type = NavType.IntType }
            )
        ) {
            SmartAddAccountScreen(
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
            route = Screen.SmartOtherBanksAccountScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(SMART_ACCOUNT) { type = SmartAccountIDNavType() },
                navArgument(TRANSFER_TYPE) { type = NavType.IntType }
            )
        ) {
            SmartAddOtherBankAccountScreen(
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
