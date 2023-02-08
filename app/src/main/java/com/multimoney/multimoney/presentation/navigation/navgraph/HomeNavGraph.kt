package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.multimoney.multimoney.presentation.navigation.HOME_ROUTE
import com.multimoney.multimoney.presentation.navigation.HOME_STATE
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountScreen
import com.multimoney.multimoney.presentation.ui.home.HomeScreen
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.HomeScreen.route,
        route = HOME_ROUTE
    ) {
        composable(route = Screen.HomeScreen.route) { backStackEntry ->
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                isRestart = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(PREVIOUS_IS_RESTART)?.observeAsState()?.value ?: true,
                homeState = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<HomeState>(HOME_STATE)?.observeAsState()?.value ?: HomeState.OLD_STATE,
                navController = navController,
                onInnerNavigate = { innerNavController, navEvent ->
                    innerNavController.navigate(navEvent.route) {
                        innerNavController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = false
                            }
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                onPopAndNavigate = {
                    navController.navigate(it.route) {
                        popUpTo(it.popTo) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(
            route = Screen.AddIbanAccountScreen.route,
            arguments = listOf(
                navArgument(ID_BRAND) {
                    type = NavType.IntType
                },
                navArgument(ID_CLIENT) {
                    type = NavType.IntType
                },
                navArgument(ID_LOAN_CLIENT) {
                    type = NavType.IntType
                }
            )
        ) {
            AddIbanAccountScreen(
                onNavigate = {
                    navController.navigate(it.route)
                },
                onPopBackStack = {
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(PREVIOUS_IS_RESTART, it.isRestart)
                    navController.getBackStackEntry(it.popTo).savedStateHandle.set(HOME_STATE, it.homeState)
                    navController.popBackStack(
                        route = it.popTo,
                        inclusive = false,
                        saveState = false
                    )
                }
            )
        }
    }
}
