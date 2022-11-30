package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.smart.SmartScreen
import com.multimoney.multimoney.presentation.ui.smart.origination.onfido.SmartOnfidoScreen

fun NavGraphBuilder.smartNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.SmartScreen.route,
        route = SMART_ROUTE
    ) {
        composable(
            Screen.SmartScreen.route,
            arguments = listOf(
                navArgument(USER) {
                    type = NavType.StringType
                },
                navArgument(ID_BRAND) {
                    type = NavType.StringType
                },
                navArgument(PK_USER) {
                    type = NavType.StringType
                },
                navArgument(IDENTIFICATION) {
                    type = NavType.StringType
                }
            )
        ) {
            SmartScreen(onNavigate = {
                navController.navigate(it.route)
            }, onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }

        composable(
            Screen.SmartOnfidoScreen.route,
            arguments = listOf(
                navArgument(USER) { type = NavType.StringType },
                navArgument(ID_BRAND) { type = NavType.IntType },
                navArgument(PK_USER) { type = NavType.LongType },
                navArgument(SIGN_DOCUMENT_ID_PRINT) { type = NavType.LongType }
            )
        ) {
            SmartOnfidoScreen(onPopAndNavigate = {
                navController.navigate(it.route) {
                    popUpTo(it.popTo) { inclusive = true }
                }
            })
        }
    }
}
