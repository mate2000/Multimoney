package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.SMART_PAYMENT_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.paymentdetails.PaymentSuccessScreen

const val EXCHANGE_AMOUNT = "exchange_amount"
const val CURRENCY_SYMBOL = "currency_symbol"

fun NavGraphBuilder.paymentSmartNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SmartPaymentCardsScreen.route,
        route = SMART_PAYMENT_ROUTE
    ) {
        composable(
            Screen.SmartPaymentCardsScreen.route,
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
            Screen.SmartPaymentSuccessScreen.route,
            arguments = listOf(
                navArgument(IS_MULTI_CURRENCY) {
                    type = NavType.BoolType
                }
            )
        ) {
            PaymentSuccessScreen(
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
    }
}
