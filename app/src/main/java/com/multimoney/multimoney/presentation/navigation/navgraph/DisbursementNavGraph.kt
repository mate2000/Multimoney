package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.DISBURSEMENT_ROUTE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SummaryListNavType
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountScreen

fun NavGraphBuilder.disbursementNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.DisbursementAmountScreen.route,
        route = DISBURSEMENT_ROUTE
    ) {
        composable(
            route = Screen.DisbursementAmountScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ID_CLIENT) { type = NavType.IntType },
                navArgument(SUMMARY_LIST) { type = SummaryListNavType() }
            )
        ) {
            DisbursementAmountScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(PREVIOUS_IS_RESTART, it.isRestart)
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
