package com.multimoney.multimoney.presentation.navigation.navgraph

import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.multimoney.multimoney.presentation.navigation.HOME_ROUTE
import com.multimoney.multimoney.presentation.navigation.HOME_STATE
import com.multimoney.multimoney.presentation.navigation.PREVIOUS_IS_RESTART
import com.multimoney.multimoney.presentation.navigation.Screen
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
                isRestart = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                    PREVIOUS_IS_RESTART
                )?.observeAsState()?.value ?: true,
                homeState = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<HomeState>(
                    HOME_STATE
                )?.observeAsState()?.value ?: HomeState.OLD_STATE,
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
                        launchSingleTop = true
                        popUpTo(it.popTo) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}
