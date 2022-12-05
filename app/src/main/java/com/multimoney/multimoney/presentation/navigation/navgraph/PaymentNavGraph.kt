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
import com.multimoney.multimoney.presentation.navigation.navtype.payment.CardVDNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.ClientBankAccountNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.PaymentMethodListNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SummaryListNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.TransferAccountNavType
import com.multimoney.multimoney.presentation.ui.credit.payment.account.PaymentAccountScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.amount.PaymentAmountScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.PaymentCardsListScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.cards.amount.PaymentAmountCardScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.fee.PaymentFeeSelectionScreen
import com.multimoney.multimoney.presentation.ui.credit.payment.location.PaymentLocationDetailsScreen
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
const val CARD_SELECTED = "summary_list"
const val CLIENT_BANK_ACCOUNT = "client_bank_account"
const val PAYMENT_METHOD = "payment_method"
const val TRANSFER_ACCOUNT = "transfer_account"
const val CREDIT_NUMBER = "credit_number"
const val REFERENCE_NUMBER = "reference_number"
const val CURRENT_AMOUNT_VALUE = "current_amount_value"
const val PAYMENT_LABEL = "currency"
const val SHOULD_DISPLAY_EXCHANGE_RATE = "should_display_exchange_rate"
const val IS_AUTOMATIC_PAYMENT_CHECKED = "is_automated_payment_checked"
const val IS_MULTI_CURRENCY = "is_multi_currency"
const val EXCHANGE_RATE_LABEL = "exchange_rate_label"
const val PAYMENT_AMOUNT = "payment_amount"
const val PAYMENT_ID = "payment_id"
const val PAYMENT_DATE = "payment_date"
const val IS_EDIT_BANK_ACCOUNT = "is_edit_bank_account"
const val IS_EDIT_PAYMENT_SCHEDULE = "is_edit_payment_schedule"
const val PREVIOUS_SCREEN = "previous_screen"

// Payment maps location parameters
const val POINT_NAME = "point_name"
const val POINT_ADDRESS = "point_address"
const val POINT_ADDRESS_DESCRIPTION = "point_address_description"
const val POINT_SCHEDULE = "point_schedule"
const val POINT_LATITUDE = "point_latitude"
const val POINT_LONGITUDE = "point_longitude"

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
                },
                navArgument(SHOULD_DISPLAY_EXCHANGE_RATE) {
                    type = NavType.BoolType
                },
                navArgument(IS_MULTI_CURRENCY) {
                    type = NavType.BoolType
                },
                navArgument(IS_AUTOMATIC_PAYMENT_CHECKED) {
                    type = NavType.BoolType
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
                },
                navArgument(IDENTIFICATION) {
                    type = NavType.StringType
                },
                navArgument(USER) {
                    type = NavType.StringType
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
                isRestart = navController.currentBackStackEntry?.savedStateHandle?.get(PREVIOUS_IS_RESTART) ?: true,
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
            route = Screen.PaymentLocationDetailsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                }
            )
        ) {
            PaymentLocationDetailsScreen(
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
            route = Screen.PaymentCardsListScreen.route,
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
            PaymentCardsListScreen(
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
            route = Screen.PaymentAmountCardsScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(IDENTIFICATION) {
                    type = NavType.StringType
                },
                navArgument(USER) {
                    type = NavType.StringType
                },
                navArgument(CARD_SELECTED) {
                    type = CardVDNavType()
                }
            )
        ) {
            PaymentAmountCardScreen(
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
    }
}
