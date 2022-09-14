package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.HOME_MAIN_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.myproducts.MyProductsBottomSheetScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductScreen
import com.multimoney.multimoney.presentation.ui.home.quickaction.QuickActionBottomSheetScreen

fun NavGraphBuilder.homeMainNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.HomeBNScreen.route,
        route = HOME_MAIN_ROUTE
    ) {
        composable(route = Screen.HomeBNScreen.route) {
            ProductScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.QuickActionBNScreen.route) {
            QuickActionBottomSheetScreen()
        }
        composable(route = Screen.ProductsBNScreen.route) {
            MyProductsBottomSheetScreen()
        }
    }
}