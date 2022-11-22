package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.ui.home.product.ProductScreen

@Composable
fun HomeInsideNavGraph(
    sharedViewModel: HomeViewModel,
    isRestart: Boolean = true,
    navController: NavHostController,
    innerNavController: NavHostController
) {
    NavHost(navController = innerNavController, startDestination = Screen.HomeBNScreen.route) {
        composable(route = Screen.HomeBNScreen.route) {
            ProductScreen(
                sharedViewModel = sharedViewModel,
                isRestart = isRestart,
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
    }
}
