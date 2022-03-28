package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TEST_ROUTE
import com.multimoney.multimoney.presentation.ui.chart.ChartScreen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreen

fun NavGraphBuilder.loginNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SplashScreen.route,
        route = TEST_ROUTE
    ) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
        composable(route = Screen.ChartScreen.route) {
            ChartScreen(
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
    }
}