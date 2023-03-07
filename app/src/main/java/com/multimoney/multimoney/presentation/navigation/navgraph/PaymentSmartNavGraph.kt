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
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNTS_ID_LIST
import com.multimoney.multimoney.presentation.navigation.SMART_PAYMENT_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TRANSFER_TYPE
import com.multimoney.multimoney.presentation.navigation.navtype.payment.CardVDNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SinpeAccountListNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SinpeAccountNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SmartAccountIDListNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SmartAccountIDNavType
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountsScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.methodsv.SmartPaymentMethodScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.optionscr.SmartPaymentOptionsContainer
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferScreen

fun NavGraphBuilder.paymentSmartNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SmartPaymentMethodScreenSV.route,
        route = SMART_PAYMENT_ROUTE
    ) {
        composable(
            Screen.SmartPaymentMethodScreenSV.route,
            arguments = listOf(
                navArgument(SMART_ACCOUNT) { type = SmartAccountIDNavType() }
            )
        ) {
            SmartPaymentMethodScreen(
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
            Screen.SmartPaymentCardsScreenSV.route,
            arguments = listOf(
                navArgument(SMART_ACCOUNT) { type = SmartAccountIDNavType() }
            )
        ) {
            SmartPaymentCardsScreen(
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
            route = Screen.SmartPaymentOptionsScreenCR.route,
            arguments = listOf(
                navArgument(SMART_ACCOUNTS_ID_LIST) {
                    type = SmartAccountIDListNavType()
                },
                navArgument(USER) { type = NavType.StringType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ID_CLIENT) { type = NavType.StringType },
                navArgument(ID_LOAN_CLIENT) { type = NavType.StringType }
            )
        ) {
            SmartPaymentOptionsContainer(
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
        composable(Screen.SavingMethodTransferScreen.route) {
            SavingMethodTransferScreen(
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
            Screen.SmartPaymentAccountScreenCR.route,
            arguments = listOf(
                navArgument(USER) { type = NavType.StringType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(IDENTIFICATION) { type = NavType.StringType },
                navArgument(PREVIOUS_SCREEN) { type = NavType.StringType },
                navArgument(ID_CLIENT) { type = NavType.StringType },
                navArgument(ID_LOAN_CLIENT) { type = NavType.StringType },
                navArgument(SMART_PAYMENT_ACCOUNTS) { type = SinpeAccountListNavType() },
                navArgument(SMART_ACCOUNT) { type = SmartAccountIDNavType() }
            )
        ) {
            SmartPaymentAccountsScreen(
                isRestart = navController.currentBackStackEntry?.savedStateHandle?.get(
                    PREVIOUS_IS_RESTART
                ) ?: true,
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
            Screen.SmartPaymentSavingAmountCR.route,
            arguments = listOf(
                navArgument(PREVIOUS_SCREEN) { type = NavType.StringType },
                navArgument(ORIGIN_ACCOUNT) { type = SinpeAccountNavType() },
                navArgument(DESTINY_ACCOUNT) { type = SmartAccountIDNavType() },
                navArgument(TRANSFER_TYPE) { type = NavType.IntType }
            )
        ) {
            SavingAmountScreen(
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
            Screen.SmartPaymentSavingAmountSV.route,
            arguments = listOf(
                navArgument(PREVIOUS_SCREEN) { type = NavType.StringType },
                navArgument(ORIGIN_ACCOUNT) { type = CardVDNavType() },
                navArgument(DESTINY_ACCOUNT) { type = SmartAccountIDNavType() },
                navArgument(TRANSFER_TYPE) { type = NavType.IntType }
            )
        ) {
            SavingAmountScreen(
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
            route = Screen.AddIbanAccountScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                },
                navArgument(ID_LOAN_CLIENT) {
                    type = NavType.IntType
                }
            )
        ) {
            AddIbanAccountScreen(
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
