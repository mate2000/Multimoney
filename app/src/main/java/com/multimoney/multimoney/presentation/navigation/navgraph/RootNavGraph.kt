package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.multimoney.multimoney.presentation.navigation.LOGIN_ROUTE
import com.multimoney.multimoney.presentation.util.LifecycleCountDownTimer

@Composable
fun Navigation(mmTimer: LifecycleCountDownTimer?) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = LOGIN_ROUTE
    ) {
        loginNavGraph(navController = navController)
        homeNavGraph(navController = navController, mmTimer)
        creditNavGraph(navController = navController)
        visaNavGraph(navController = navController)
        paymentNavGraph(navController = navController)
        smartNavGraph(navController = navController)
        testNavGraph(navController = navController)
    }
}
