package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.VISA_ROUTE
import com.multimoney.multimoney.presentation.ui.visa.card.VisaCardScreen
import com.multimoney.multimoney.presentation.ui.visa.issuance.VisaIssuanceScreen

fun NavGraphBuilder.visaNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.VisaIssuanceScreen.route,
        route = VISA_ROUTE
    ) {
        composable(route = Screen.VisaIssuanceScreen.route) { navBackStackEntry ->
            VisaIssuanceScreen(
                onPopBackStack = { navController.popBackStack() },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.VisaCardScreen.route) { navBackStackEntry ->
            VisaCardScreen(
                onPopBackStack = { navController.popBackStack() },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
    }
}
