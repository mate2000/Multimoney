package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TEST_ROUTE
import com.multimoney.multimoney.presentation.ui.test.chart.ChartScreen
import com.multimoney.multimoney.presentation.ui.test.subscription.SubscriptionScreen

fun NavGraphBuilder.testNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.TestScreen.route,
        route = TEST_ROUTE
    ) {
        composable(
            route = Screen.TestScreen.route
        ) {
            SubscriptionScreen()
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
