package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.IBAN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.ID_VISA_CARD
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.SMART_IDS_LIST
import com.multimoney.multimoney.presentation.navigation.SMART_PAYMENT_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SinpeAccountListNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SinpeAccountNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SmartAccountIDListNavType
import com.multimoney.multimoney.presentation.navigation.navtype.payment.SmartAccountIDNavType
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountsScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.methodsv.SmartPaymentMethodScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.optionscr.SmartPaymentOptionsScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferScreen

const val MASKED_CARD = "masked_card"
const val BANK_DETAIL = "bank_detail"

fun NavGraphBuilder.paymentSmartNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SmartPaymentMethodScreenSV.route,
        route = SMART_PAYMENT_ROUTE
    ) {
        composable(
            Screen.SmartPaymentMethodScreenSV.route,
            arguments = listOf(
                navArgument(SMART_IDS) { type = SmartAccountIDNavType() }
            )
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
            Screen.SmartPaymentCardsScreenSV.route,
            arguments = listOf(
                navArgument(SMART_IDS) { type = SmartAccountIDNavType() }
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
            route = Screen.SmartPaymentOptionsScreenCR.route,
            arguments = listOf(
                navArgument(SMART_IDS_LIST) {
                    type = SmartAccountIDListNavType()
                },
                navArgument(USER) { type = NavType.StringType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ID_CLIENT) { type = NavType.StringType },
                navArgument(ID_LOAN_CLIENT) { type = NavType.StringType }
            )
        ) {
            SmartPaymentOptionsScreen(
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
            Screen.SmartPaymentAccountScreenCR.route,
            arguments = listOf(
                navArgument(USER) { type = NavType.StringType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(IDENTIFICATION) { type = NavType.StringType },
                navArgument(PREVIOUS_SCREEN) { type = NavType.StringType },
                navArgument(ID_CLIENT) { type = NavType.StringType },
                navArgument(ID_LOAN_CLIENT) { type = NavType.StringType },
                navArgument(SMART_PAYMENT_ACCOUNTS) { type = SinpeAccountListNavType() },
                navArgument(SMART_IDS) { type = SmartAccountIDNavType() }
            )
        ) {
            SmartPaymentAccountsScreen(
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
            Screen.SmartPaymentSavingAmount.route,
            arguments = listOf(
                navArgument(SMART_IDS) { type = SmartAccountIDNavType() },
                navArgument(IBAN_ACCOUNT) { type = SinpeAccountNavType() },
                navArgument(ID_VISA_CARD) { type = NavType.LongType },
                navArgument(PREVIOUS_SCREEN) { type = NavType.StringType },
                navArgument(MASKED_CARD) { type = NavType.StringType },
                navArgument(BANK_DETAIL) { type = NavType.StringType }
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
