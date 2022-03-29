package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.LOGIN_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.login.LoginScreen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreen
import com.multimoney.multimoney.presentation.ui.test.TestScreen

fun NavGraphBuilder.loginNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SplashScreen.route,
        route = LOGIN_ROUTE
    ) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
        composable(route = Screen.TestScreen.route) {
            TestScreen(
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
    }
}