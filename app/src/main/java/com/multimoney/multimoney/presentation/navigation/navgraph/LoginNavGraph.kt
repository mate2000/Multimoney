package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.LOGIN_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.login.signin.SignInScreen
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpScreen
import com.multimoney.multimoney.presentation.ui.login.signup.completed.SignUpCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.splash.DEFAULT_STEP
import com.multimoney.multimoney.presentation.ui.login.signup.splash.SignUpSplashComeBack
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingScreen
import com.multimoney.multimoney.presentation.ui.payment.paymentvoucher.PaymentVoucherScreen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreen

const val SIGN_UP_STEP = "sign_up_step"

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
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                },
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(
                // Workaround to solve compose issue when launchSingleTop is combine with arguments
                // (Use: navController.currentBackStackEntry ?: navBackStackEntry)
                step = navController.currentBackStackEntry?.arguments?.getString(SIGN_UP_STEP, DEFAULT_STEP)
                    ?: DEFAULT_STEP,
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
        composable(route = Screen.SignUpSplashComeBackScreen.route) {
            SignUpSplashComeBack(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.SignUpCompleted.route) {
            SignUpCompleted(
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.PaymentVoucherScreen.route) {
            PaymentVoucherScreen()
        }
    }
}
