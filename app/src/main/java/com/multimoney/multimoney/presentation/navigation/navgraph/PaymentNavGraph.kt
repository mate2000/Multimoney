package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.PAYMENT_CREDIT_ROUTE
import com.multimoney.multimoney.presentation.navigation.SUMMARY_LIST
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountScreen
import com.multimoney.multimoney.presentation.ui.payment.fee.PaymentFeeSelectionScreen
import com.multimoney.multimoney.presentation.util.customnavtype.SummaryListNavType

fun NavGraphBuilder.paymentNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.PaymentFeeScreen.route, route = PAYMENT_CREDIT_ROUTE
    ) {
        composable(
            Screen.PaymentFeeScreen.route,
            arguments = listOf(navArgument(SUMMARY_LIST) { type = SummaryListNavType() })
        ) { navBackStackEntry ->
            PaymentFeeSelectionScreen(navBackStackEntry = navBackStackEntry, onNavigate = {
                navController.navigate(it.route)
            }, onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }
        composable(
            Screen.PaymentAccountScreen.route
        ) { navBackStackEntry ->
            PaymentAccountScreen(navBackStackEntry = navBackStackEntry, onNavigate = {
                navController.navigate(it.route)
            }, onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }
    }
}
