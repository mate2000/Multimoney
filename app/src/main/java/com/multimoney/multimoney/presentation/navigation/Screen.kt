package com.multimoney.multimoney.presentation.navigation

const val TEST_ROUTE = "test_route"

/**
 * Use this class to declare all your new screens and their routes
 * **/
sealed class Screen(val route: String) {
    object TestScreen : Screen("test_screen")
    object SplashScreen: Screen("splash_screen")
    object ChartScreen : Screen("chart_screen")
}
