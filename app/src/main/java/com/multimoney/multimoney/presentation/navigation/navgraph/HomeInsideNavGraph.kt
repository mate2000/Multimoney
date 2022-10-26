package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.product.ProductScreen

@Composable
fun HomeInsideNavGraph(navController: NavHostController, innerNavController: NavHostController) {
    NavHost(navController = innerNavController, startDestination = Screen.HomeBNScreen.route) {
        composable(route = Screen.HomeBNScreen.route) {
            ProductScreen(
                isRestart = navController.currentBackStackEntry?.savedStateHandle?.get(PREVIOUS_IS_RESTART) ?: true,
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
    }
}
