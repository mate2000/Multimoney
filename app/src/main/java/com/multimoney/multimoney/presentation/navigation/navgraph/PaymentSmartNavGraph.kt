package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.ID_VISA_CARD
import com.multimoney.multimoney.presentation.navigation.PAYMENT_SMART_ROUTE
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SinpeAccountNavType
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountsScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.options.SmartPaymentOptionsScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferScreen

fun NavGraphBuilder.paymentSmartNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.PaymentSmartCardsScreen.route,
        route = PAYMENT_SMART_ROUTE
    ) {
        composable(
            Screen.PaymentSmartCardsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                }
            )
        ) {
            SmartPaymentCardsScreen(
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
            Screen.SmartPaymentScreen.route
        ) {
            SmartPaymentMethodScreen(
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
            route = Screen.SmartPaymentOptionsScreen.route
        ) {
            SmartPaymentOptionsScreen(onNavigate = {
                navController.navigate(it.route)
            }, onPopAndNavigate = {
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
            Screen.SmartPaymentAccountScreen.route,
            arguments = listOf(
                navArgument(SMART_PAYMENT_ACCOUNTS) {
                    type = SinpeAccountNavType()
                }
            )
        ) {
            SmartPaymentAccountsScreen(onNavigate = {
                navController.navigate(it.route)
            }, onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                })
        }

        composable(
            Screen.SmartSavingAmount.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_VISA_CARD) {
                    type = NavType.IntType
                },
                navArgument(USER) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(IDENTIFICATION) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            SavingAmountScreen(
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
