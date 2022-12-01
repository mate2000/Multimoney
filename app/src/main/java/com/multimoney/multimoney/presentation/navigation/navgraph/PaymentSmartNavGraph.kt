package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PAYMENT_SMART_ROUTE
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsScreen

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
                },
                navArgument(IDENTIFICATION) {
                    type = NavType.StringType
                },
                navArgument(USER) {
                    type = NavType.StringType
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
    }
}
