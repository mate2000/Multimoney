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
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountScreen
import com.multimoney.multimoney.presentation.ui.credit.disbursement.addaccount.DisbursementAddAccountScreen
import com.multimoney.multimoney.presentation.ui.credit.disbursement.amount.DisbursementAmountScreen

const val NEXT_PAYMENT_DATE = "next_payment_date"
const val QUOTA_TOTAL = "quota_total"
const val SELECTED_AMOUNT = "selected_amount"

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
                navArgument(SUMMARY_LIST) { type = SummaryListNavType() },
                navArgument(ID_USER_REQUEST) { type = NavType.IntType },
                navArgument(IDENTIFICATION) { type = NavType.StringType }
            )
        ) {
            DisbursementAmountScreen(
                isRestart = navController.currentBackStackEntry?.savedStateHandle?.get(PREVIOUS_IS_RESTART) ?: true,
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

        composable(
            route = Screen.DisbursementAccountScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ID_CLIENT) { type = NavType.IntType },
                navArgument(ID_LOAN_CLIENT) { type = NavType.LongType },
                navArgument(ID_USER_REQUEST) { type = NavType.IntType },
                navArgument(FK_FLOW_CONTROL) { type = NavType.IntType },
                navArgument(CURRENCY_ID) { type = NavType.IntType }
            )
        ) {
            DisbursementAccountScreen(
                isRestart = navController.currentBackStackEntry?.savedStateHandle?.get(PREVIOUS_IS_RESTART) ?: true,
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

        composable(
            route = Screen.DisbursementAddAccountScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ID_USER_REQUEST) { type = NavType.IntType },
                navArgument(ID_CLIENT) { type = NavType.IntType },
                navArgument(ID_LOAN_CLIENT) { type = NavType.IntType },
                navArgument(ID_CURRENCY) { type = NavType.IntType }
            )
        ) {
            DisbursementAddAccountScreen(
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
