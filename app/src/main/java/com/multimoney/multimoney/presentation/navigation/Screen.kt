package com.multimoney.multimoney.presentation.navigation

import com.multimoney.multimoney.presentation.navigation.navgraph.SIGN_UP_STEP
import com.multimoney.multimoney.presentation.navigation.navgraph.USER_EMAIL_ARG_KEY
import com.multimoney.multimoney.presentation.navigation.navgraph.USER_PASSWORD_ARG_KEY

const val LOGIN_ROUTE = "log_in_route"
const val HOME_ROUTE = "home_route"
const val TEST_ROUTE = "test_route"

/**
 * Use this class to declare all your new screens and their routes
 * **/
sealed class Screen(val route: String, val baseRoute: String = "") {

    // LoginNavGraph Screens
    object SplashScreen : Screen("splash_screen")
    object OnBoardingScreen : Screen("onboarding_screen")
    object SignInScreen : Screen("sign_in_screen")
    object SignUpScreen : Screen("sign_up_screen/{$SIGN_UP_STEP}", "sign_up_screen")
    object SignUpBiometricsScreen : Screen(
        "sign_up_biometrics_screen/{$USER_EMAIL_ARG_KEY}/{$USER_PASSWORD_ARG_KEY}",
        "sign_up_biometrics_screen"
    )

    object SignUpSplashComeBackScreen :
        Screen(
            "sign_up_splash_come_back_scree/{$SIGN_UP_STEP}",
            "sign_up_splash_come_back_scree"
        )

    object SignUpBiometricsFailureScreen : Screen("sign_up_biometrics_failure")

    object SignUpCompleted : Screen("sign_up_completed")

    // HomeNavGraph Screens
    object HomeScreen : Screen("home_screen")

    // TestNavGraph Screens
    object TestScreen : Screen("test_screen")
    object ChartScreen : Screen("chart_screen")
}
