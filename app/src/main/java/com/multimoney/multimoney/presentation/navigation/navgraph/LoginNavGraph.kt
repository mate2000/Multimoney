package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.LOGIN_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.login.signin.SignInScreen
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpScreen
import com.multimoney.multimoney.presentation.ui.login.signup.biometrics.SignUpBiometricsScreen
import com.multimoney.multimoney.presentation.ui.login.signup.biometrics.SignUpComplete
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingScreen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreen

fun NavGraphBuilder.loginNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SplashScreen.route,
        route = LOGIN_ROUTE
    ) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.OnBoardingScreen.route) {
            OnBoardingScreen(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.BiometricsSetUp.route) {
            SignUpBiometricsScreen(onNavigate = {
                navController.navigate(it.route)
            })
        }
        composable(route = Screen.SignUpComplete.route) {
            SignUpComplete(
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
    }
}