package com.multimoney.multimoney.presentation.navigation

const val LOGIN_ROUTE = "log_in_route"
const val TEST_ROUTE = "test_route"

/**
 * Use this class to declare all your new screens and their routes
 * **/
sealed class Screen(val route: String) {

    // LoginNavGraph Screens
    object SplashScreen : Screen("splash_screen")
    object OnBoardingScreen : Screen("onboarding_screen")
    object SignInScreen : Screen("sign_in_screen")
    object SignUpScreen : Screen("sign_up_screen")
    object BiometricsSetUp : Screen("biometrics_setup")
    object SignUpComplete: Screen("sign_up_complete")

    // TestNavGraph Screens
    object TestScreen : Screen("test_screen")
    object ChartScreen : Screen("chart_screen")
}
