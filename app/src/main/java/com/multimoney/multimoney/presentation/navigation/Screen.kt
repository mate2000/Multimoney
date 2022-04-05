package com.multimoney.multimoney.presentation.navigation

const val LOGIN_ROUTE = "login_route"
const val TEST_ROUTE = "test_route"

/**
 * Use this class to declare all your new screens and their routes
 * **/
sealed class Screen(val route: String) {

    // LoginNavGraph Screens
    object SplashScreen : Screen("splash_screen")
    object LoginScreen : Screen("login_screen")

    // TestNavGraph Screens
    object TestScreen : Screen("test_screen")
    object ChartScreen : Screen("chart_screen")
    object OnBoardingScreen: Screen("onboarding_screen")
}
