package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.VISA_ROUTE
import com.multimoney.multimoney.presentation.navigation.navtype.payment.CardInformationNavType
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardScreen
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceScreen
import com.multimoney.multimoney.presentation.ui.visa.novotokenization.VisaTokenizationWaitingScreen

const val CARD_INFORMATION = "card_information"
const val BALANCE_CARD_INFORMATION = "balance_card_information"
const val AVAILABLE_BALANCE_LABEL = "available_balance_label"

fun NavGraphBuilder.visaNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.VisaIssuanceScreen.route,
        route = VISA_ROUTE
    ) {
        composable(
            route = Screen.VisaIssuanceScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(CARD_INFORMATION) { type = CardInformationNavType() }
            )
        ) { navBackStackEntry ->
            VisaIssuanceScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.VisaCardScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(CARD_INFORMATION) { type = CardInformationNavType() }
            )
        ) { navBackStackEntry ->
            VisaCardScreen(
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
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
    }
    composable(
        route = Screen.VisaTokenizationWaitingScreen.route,
        arguments = listOf(
            navArgument(ID_BRAND) { type = NavType.IntType },
            navArgument(PK_USER) { type = NavType.LongType },
            navArgument(CARD_INFORMATION) { type = CardInformationNavType() }
        )
    ) {
        VisaTokenizationWaitingScreen(onPopAndNavigate = {
            navController.navigate(it.route) {
                launchSingleTop = true
                popUpTo(it.popTo) { inclusive = true }
            }
        })
    }
}
