package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.myproducts.MyProductsBottomSheetScreen
import com.multimoney.multimoney.presentation.ui.home.product.ProductScreen
import com.multimoney.multimoney.presentation.ui.home.quickaction.QuickActionBottomSheetScreen

@Composable
fun HomeInsideNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.HomeBNScreen.route) {
        composable(route = Screen.HomeBNScreen.route) {
            ProductScreen(
                onNavigate = {
                    navController.navigate(it.route)
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