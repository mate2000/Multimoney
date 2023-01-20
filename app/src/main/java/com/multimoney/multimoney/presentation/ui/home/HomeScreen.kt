package com.multimoney.multimoney.presentation.ui.home

import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.multimoney.multimoney.R.drawable
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.extension.findActivity
import com.multimoney.multimoney.presentation.navigation.BottomNavItem
import com.multimoney.multimoney.presentation.navigation.navgraph.HomeInsideNavGraph
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnHideAutomaticPaymentEditBottomSheet
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.BaseEvent.OnShowAutomaticPaymentEditBottomSheet
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnCloseCardIssuanceError
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnDeleteAutomaticPayment
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnEditAutomaticPayment
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnHideUnlinkToast
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnSetHomeState
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnSetUserData
import com.multimoney.multimoney.presentation.ui.home.myproducts.MyProductsBottomSheetScreen
import com.multimoney.multimoney.presentation.ui.home.product.credit.uisections.AutomaticPaymentEditBottomSheet
import com.multimoney.multimoney.presentation.ui.home.quickaction.QuickActionBottomSheetScreen
import com.multimoney.multimoney.presentation.uielement.AlertResult
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.MMCountDownTimer.OnCountDownTimerEvents
import com.multimoney.multimoney.presentation.util.NavEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    isRestart: Boolean = true,
    homeState: HomeState,
    navController: NavHostController,
    onInnerNavigate: (innerNavController: NavHostController, NavEvent.InnerNavigate) -> Unit = { _, _ -> },
    onPopAndNavigate: (NavEvent.PopAndNavigate) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    viewModel.apply {
        isOnRestart = isRestart
        LaunchedEffect(isOnRestart) {
            if (isOnRestart) {
                onUIEvent(OnSetUserData)
                isOnRestart = false
            }
        }
        LaunchedEffect(key1 = homeState) {
            onUIEvent(OnSetHomeState(homeState))
        }
    }

    val innerNavController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val automaticPaymentEditBottomSheetState = rememberModalBottomSheetState(Hidden)
    val quickActionsModalBottomSheetState =
        rememberModalBottomSheetState(initialValue = Hidden, skipHalfExpanded = true)
    val myProductsModalBottomSheetState = rememberModalBottomSheetState(Hidden, skipHalfExpanded = true)
    val activity = LocalContext.current.findActivity()
    val unlinkedToastText = stringResource(id = string.card_preferences_unlinked_card_toast)

    LaunchedEffect(true) {
        viewModel.executeNavigation(
            onInnerNavigate = onInnerNavigate,
            onPopAndNavigate = onPopAndNavigate
        )
        viewModel.countDownTimer.subscribe(object : OnCountDownTimerEvents {
            override fun onFinished() {
                viewModel.onUIEvent(
                    HomeViewModel.UIEvent.OnSignOut(activity)
                )
            }

            override fun onMaxTimeUsed(millisMainUntilFinished: Long) {
                viewModel.onUIEvent(HomeViewModel.UIEvent.OnShowTimerDialog(millisMainUntilFinished, activity))
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
                is HomeViewModel.BaseEvent.OnStartCountDownTimer -> viewModel.countDownTimer.startTimer(
                    event.millisInFuture
                )
                is OnShowAutomaticPaymentEditBottomSheet -> {
                    coroutineScope.launch {
                        automaticPaymentEditBottomSheetState.show()
                    }
                }
                is OnHideAutomaticPaymentEditBottomSheet -> {
                    coroutineScope.launch {
                        automaticPaymentEditBottomSheetState.hide()
                    }
                }
            }
        }
    }

    if (viewModel.uiState.toastIsVisible) {
        Toast.makeText(activity, unlinkedToastText, Toast.LENGTH_LONG).show()
        viewModel.onUIEvent(OnHideUnlinkToast)
    }

    Scaffold(bottomBar = {
        MMBottomNavigation(
            navController = innerNavController,
            viewModel
        )
    }) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            HomeInsideNavGraph(
                sharedViewModel = viewModel,
                navController = navController,
                innerNavController = innerNavController
            )
        }
    }

    QuickActionBottomSheetScreen(viewModel, coroutineScope, quickActionsModalBottomSheetState)
    MyProductsBottomSheetScreen(viewModel, coroutineScope, myProductsModalBottomSheetState)
    AutomaticPaymentEditBottomSheet(
        coroutineScope = coroutineScope,
        modalBottomSheetState = automaticPaymentEditBottomSheetState,
        onEditClick = { viewModel.onUIEvent(OnEditAutomaticPayment) },
        onDeleteClick = { viewModel.onUIEvent(OnDeleteAutomaticPayment) }
    )

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

    if (viewModel.uiState.showCardIssuanceError) {
        AlertResult(
            iconResource = drawable.ic_error_symbol,
            titleResource = string.card_issuance_error_title,
            descriptionResource = viewModel.getCardIssuanceDescriptionError(),
            buttonTextResource = string.understood,
            isLeftButtonVisible = false,
            onRightButtonClick = { viewModel.onUIEvent(OnCloseCardIssuanceError) },
            onButtonClick = { viewModel.onUIEvent(OnCloseCardIssuanceError) }
        )
    }

    if (viewModel.uiState.openDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.openDialog.titleResource),
            message = stringResource(id = viewModel.uiState.openDialog.descriptionResource).ifEmpty { viewModel.uiState.openDialog.description },
            positiveButtonText = stringResource(id = viewModel.uiState.openDialog.positiveResource),
            openDialogCustom = viewModel.uiState.openDialog.isActive,
            onPositiveAction = viewModel.uiState.openDialog.positiveAction,
            isCancelable = viewModel.uiState.openDialog.isCancelable
        )
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
