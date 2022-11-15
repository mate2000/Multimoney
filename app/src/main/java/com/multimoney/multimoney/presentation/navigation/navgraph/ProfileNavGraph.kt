package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.PROFILE_ROUTE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.home.profile.ProfileScreen

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.ProfileScreen.route,
        route = PROFILE_ROUTE
    ) {
        composable(Screen.ProfileScreen.route) {
            ProfileScreen(
                onPopBackStack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                },
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }
    }
}
