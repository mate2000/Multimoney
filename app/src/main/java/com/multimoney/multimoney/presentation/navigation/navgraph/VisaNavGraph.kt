package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.VISA_ROUTE
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardScreen
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceScreen

fun NavGraphBuilder.visaNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.VisaIssuanceScreen.route,
        route = VISA_ROUTE
    ) {
        composable(route = Screen.VisaIssuanceScreen.route, arguments = listOf(
            navArgument(ID_BRAND){ },
            navArgument(ID_CLIENT){},
            navArgument(ID_LOAN_CLIENT){}
        )) { navBackStackEntry ->
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
        composable(route = Screen.VisaCardScreen.route) { navBackStackEntry ->
            VisaCardScreen(
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
}
