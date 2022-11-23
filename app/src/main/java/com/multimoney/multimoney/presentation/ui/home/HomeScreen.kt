package com.multimoney.multimoney.presentation.ui.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.multimoney.multimoney.presentation.navigation.BottomNavItem
import com.multimoney.multimoney.presentation.navigation.navgraph.HomeInsideNavGraph
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.myproducts.MyProductsBottomSheetScreen
import com.multimoney.multimoney.presentation.ui.home.quickaction.QuickActionBottomSheetScreen
import com.multimoney.multimoney.presentation.util.MMCountDownTimer.OnCountDownTimerFinish
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onInnerNavigate: (innerNavController: NavHostController, NavEvent.InnerNavigate) -> Unit = { _, _ -> },
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val innerNavController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val quickActionsModalBottomSheetState = rememberModalBottomSheetState(Hidden)
    val myProductsModalBottomSheetState = rememberModalBottomSheetState(Hidden)
    val activity = (LocalContext.current as? Activity)

    LaunchedEffect(true) {
        viewModel.executeNavigation(onInnerNavigate = onInnerNavigate, onPopAndNavigate = onPopAndNavigate)
        viewModel.countDownTimer.subscribe(object : OnCountDownTimerFinish {
            override fun onFinished() {
                viewModel.onUIEvent(HomeViewModel.UIEvent.OnSignOut)
            }
        })
        viewModel.baseEvent.collect { event ->
            when (event) {
                is HomeViewModel.BaseEvent.OnOpenQuickActionsBottomSheet -> {
                    coroutineScope.launch {
                        quickActionsModalBottomSheetState.show()
                    }
                }
                is HomeViewModel.BaseEvent.OnOpenMyProductsBottomSheet -> {
                    coroutineScope.launch {
                        myProductsModalBottomSheetState.show()
                    }
                }
            }
        }
    }

    Scaffold(bottomBar = { MMBottomNavigation(navController = innerNavController, viewModel) }) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            HomeInsideNavGraph(navController = navController, innerNavController = innerNavController)
        }
    }

    QuickActionBottomSheetScreen(viewModel, coroutineScope, quickActionsModalBottomSheetState)
    MyProductsBottomSheetScreen(viewModel, coroutineScope, myProductsModalBottomSheetState)

    BackHandler {
        when {
            quickActionsModalBottomSheetState.isVisible -> {
                coroutineScope.launch {
                    quickActionsModalBottomSheetState.hide()
                }
            }
            myProductsModalBottomSheetState.isVisible -> {
                coroutineScope.launch {
                    myProductsModalBottomSheetState.hide()
                }
            }
            else -> {
                activity?.finish()
            }
        }
    }
}

@Composable
fun MMBottomNavigation(navController: NavHostController, viewModel: HomeViewModel) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.QuickAction,
        BottomNavItem.Products
    )

    Column {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp),
            color = MultimoneyTheme.colors.bottomNavigationDividerColor
        )
        BottomNavigation(
            modifier = Modifier.height(76.dp),
            backgroundColor = MultimoneyTheme.colors.background,
            contentColor = MultimoneyTheme.colors.bottomNavigationIconSelectedColor
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                BottomNavigationItem(
                    icon = { Icon(painterResource(id = item.icon), contentDescription = "") },
                    selectedContentColor = MultimoneyTheme.colors.bottomNavigationIconSelectedColor,
                    unselectedContentColor = MultimoneyTheme.colors.bottomNavigationIconUnselectedColor,
                    alwaysShowLabel = false,
                    selected = currentRoute == item.route,
                    onClick = {
                        viewModel.onUIEvent(
                            HomeViewModel.UIEvent.OnBottomNavigationItemClick(
                                navController,
                                item.route
                            )
                        )
                    }
                )
            }
        }
    }
}
