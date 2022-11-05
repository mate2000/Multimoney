package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.HOME_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.HomeScreen
import com.multimoney.multimoney.presentation.util.LifecycleCountDownTimer

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    mmTimer: LifecycleCountDownTimer?
) {
    navigation(
        startDestination = Screen.HomeScreen.route,
        route = HOME_ROUTE
    ) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                onInnerNavigate = { innerNavController, navEvent ->
                    innerNavController.navigate(navEvent.route) {
                        innerNavController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = false
                            }
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                },
                mmTimer
            )
        }
    }
}
