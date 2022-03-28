package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.multimoney.multimoney.presentation.navigation.TEST_ROUTE

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = TEST_ROUTE
    ) {
        loginNavGraph(navController = navController)
    }
}