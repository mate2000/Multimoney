package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PAYMENT_CREDIT_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.ClientBankAccountNavType
import com.multimoney.multimoney.presentation.navigation.navtype.SummaryListNavType
import com.multimoney.multimoney.presentation.ui.payment.account.PaymentAccountScreen
import com.multimoney.multimoney.presentation.ui.payment.amount.PaymentAmountScreen
import com.multimoney.multimoney.presentation.ui.payment.fee.PaymentFeeSelectionScreen
import com.multimoney.multimoney.presentation.ui.payment.paymentvoucher.PaymentVoucherScreen

// payment process parameters
const val USER = "user"
const val ID_CLIENT = "id_client"
const val ID_LOAN_CLIENT = "id_loan_client"
const val CURRENCY = "currency"
const val ID_CURRENCY = "id_currency"
const val SUMMARY_LIST = "summary_list"
const val CLIENT_BANK_ACCOUNT = "client_bank_account"

fun NavGraphBuilder.paymentNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.PaymentFeeScreen.route,
        route = PAYMENT_CREDIT_ROUTE
    ) {
        composable(
            Screen.PaymentFeeScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                },
                navArgument(ID_LOAN_CLIENT) {
                    type = NavType.IntType
                },
                navArgument(SUMMARY_LIST) {
                    type = SummaryListNavType()
                }
            )
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
            Screen.PaymentAccountScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                },
                navArgument(ID_LOAN_CLIENT) {
                    type = NavType.IntType
                },
                navArgument(SUMMARY_LIST) {
                    type = SummaryListNavType()
                }
            )
        ) { navBackStackEntry ->
            PaymentAccountScreen(navBackStackEntry = navBackStackEntry, onNavigate = {
                navController.navigate(it.route)
            }, onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                })
        }
        composable(
            route = Screen.PaymentAmountScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                },
                navArgument(ID_LOAN_CLIENT) {
                    type = NavType.IntType
                },
                navArgument(SUMMARY_LIST) {
                    type = SummaryListNavType()
                },
                navArgument(CLIENT_BANK_ACCOUNT) {
                    type = ClientBankAccountNavType()
                }
            )
        ) { navBackStackEntry ->
            PaymentAmountScreen(
                navBackStackEntry = navBackStackEntry,
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.PaymentVoucherScreen.route) {
            PaymentVoucherScreen()
        }
    }
}
