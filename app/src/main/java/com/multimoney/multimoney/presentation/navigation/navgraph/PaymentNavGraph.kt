package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PAYMENT_CREDIT_ROUTE
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.payment.ClientBankAccountNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.PaymentMethodListNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SummaryListNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.TransferAccountNavType
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.fee.PaymentFeeSelectionScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.options.PaymentOptionsScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.paymentvoucher.PaymentVoucherScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.points.PaymentPointsScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.PaymentScheduleScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.schedule.account.PaymentScheduleAccountScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.transfer.PaymentOptionsTransferScreen

// payment process parameters
const val USER = "user"
const val ID_CLIENT = "id_client"
const val NAME_CLIENT = "name_client"
const val ID_LOAN_CLIENT = "id_loan_client"
const val SUMMARY_LIST = "summary_list"
const val CLIENT_BANK_ACCOUNT = "client_bank_account"
const val PAYMENT_METHOD = "payment_method"
const val TRANSFER_ACCOUNT = "transfer_account"
const val CREDIT_NUMBER = "credit_number"
const val PAYMENT_DATE = "payment_date"
const val IS_EDIT_BANK_ACCOUNT = "is_edit_bank_account"
const val IS_EDIT_PAYMENT_SCHEDULE = "is_edit_payment_schedule"
const val PREVIOUS_SCREEN = "previous_screen"

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
                },
                navArgument(IDENTIFICATION) {
                    type = NavType.StringType
                },
                navArgument(NAME_CLIENT) {
                    type = NavType.StringType
                }
            )
        ) {
            PaymentFeeSelectionScreen(
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
                },
                navArgument(IDENTIFICATION) {
                    type = NavType.StringType
                },
                navArgument(NAME_CLIENT) {
                    type = NavType.StringType
                }
            )
        ) {
            PaymentAccountScreen(
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
        ) {
            PaymentAmountScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
        composable(
            Screen.PaymentScheduleScreen.route,
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
                navArgument(CLIENT_BANK_ACCOUNT) {
                    type = ClientBankAccountNavType()
                },
                navArgument(IS_EDIT_BANK_ACCOUNT) {
                    type = NavType.BoolType
                },
                navArgument(IS_EDIT_PAYMENT_SCHEDULE) {
                    type = NavType.BoolType
                }
            )
        ) {
            PaymentScheduleScreen(
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
            Screen.PaymentScheduleAccountScreen.route,
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
            PaymentScheduleAccountScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
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
            route = Screen.PaymentVoucherScreen.route,
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
                navArgument(CLIENT_BANK_ACCOUNT) {
                    type = ClientBankAccountNavType()
                }
            )
        ) {
            PaymentVoucherScreen(
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
            route = Screen.PaymentOptionsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(PAYMENT_METHOD) {
                    type = PaymentMethodListNavType()
                },
                navArgument(TRANSFER_ACCOUNT) {
                    type = TransferAccountNavType()
                }
            )
        ) {
            PaymentOptionsScreen(
                onNavigate = { navController.navigate(it.route) },
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
            route = Screen.PaymentOptionsTransferScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(TRANSFER_ACCOUNT) {
                    type = TransferAccountNavType()
                }
            )
        ) {
            PaymentOptionsTransferScreen(
                onNavigate = { navController.navigate(it.route) },
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
            route = Screen.PaymentPointsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                }
            )
        ) {
            PaymentPointsScreen(
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
