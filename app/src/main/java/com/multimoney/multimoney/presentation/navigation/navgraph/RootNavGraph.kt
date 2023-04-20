package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.multimoney.multimoney.presentation.navigation.LOGIN_ROUTE

@Composable
fun Navigation(notificationState: MutableState<Boolean>) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = LOGIN_ROUTE
    ) {
        loginNavGraph(navController = navController)
        homeNavGraph(navController = navController, notificationState)
        creditNavGraph(navController = navController)
        disbursementNavGraph(navController = navController)
        visaNavGraph(navController = navController)
        paymentNavGraph(navController = navController)
        smartNavGraph(navController = navController)
        cryptoNavGraph(navController = navController)
        profileNavGraph(navController = navController)
        paymentSmartNavGraph(navController = navController)
        smartTransferNavGraph(navController = navController)
    }
}
