package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.SMART_MOVEMENTS_ROUTE
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.product.smart.movements.SmartMovementsScreen

const val ACCOUNT_TOKEN = "account_token"

fun NavGraphBuilder.smartMovementsNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SmartMovementsScreen.route,
        route = SMART_MOVEMENTS_ROUTE
    ) {
        composable(
            Screen.SmartMovementsScreen.route,
            arguments = listOf(
                navArgument(USER) {
                    type = NavType.StringType
                },
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(IDENTIFICATION) {
                    type = NavType.StringType
                },
                navArgument(ACCOUNT_TOKEN) {
                    type = NavType.StringType
                }
            )
        ) {
            SmartMovementsScreen(
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
    }
}