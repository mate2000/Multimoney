package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.NavType.Companion
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.ID_VISA_CARD
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.SMART_PAYMENT_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.payment.accounts.SmartPaymentAccountScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.amount.SavingAmountScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.cards.SmartPaymentCardsScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.method.SmartPaymentMethodScreen
import com.multimoney.multimoney.presentation.ui.smart.payment.transfer.SavingMethodTransferScreen

const val EXCHANGE_AMOUNT = "exchange_amount"
const val CURRENCY_SYMBOL = "currency_symbol"
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
                navArgument(ID_CURRENCY) { type = NavType.IntType },
                navArgument(ACCOUNT_TOKEN) { type = Companion.LongType }
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
            Screen.SmartPaymentCardsScreen.route,
            arguments = listOf(
                navArgument(ACCOUNT_TOKEN) { type = NavType.LongType },
                navArgument(ID_CURRENCY) { type = NavType.IntType }
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
            route = Screen.SmartPaymentAccountScreenCR.route
        ) {
            SmartPaymentAccountScreen(onNavigate = {
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
            Screen.SmartPaymentSavingAmount.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(ACCOUNT_TOKEN) { type = NavType.LongType },
                navArgument(ID_CURRENCY) { type = NavType.IntType },
                navArgument(ID_VISA_CARD) { type = NavType.LongType }
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
