package com.multimoney.multimoney.presentation.ui.home

import androidx.navigation.NavHostController
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.Screen.HomeBNScreen
import com.multimoney.multimoney.presentation.navigation.Screen.ProductsBNScreen
import com.multimoney.multimoney.presentation.navigation.Screen.QuickActionBNScreen
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnBottomNavigationItemClick
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnSignOut
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel(true) {

    fun navigation(innerNavHostController: NavHostController, route: String) {
        when (route) {
            HomeBNScreen.route -> {
                innerNavigateTo(innerNavHostController, route)
            }
            QuickActionBNScreen.route -> {
                emitBaseEvent(BaseEvent.OnOpenQuickActionsBottomSheet)
            }
            ProductsBNScreen.route -> {
                emitBaseEvent(BaseEvent.OnOpenMyProductsBottomSheet)
            }
        }
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnBottomNavigationItemClick -> navigation(uiEvent.innerNavHostController, uiEvent.route)
            is OnSignOut -> popAndNavigateTo(Screen.SignInScreen.route, Screen.HomeScreen.route)
        }
    }

    sealed class UIEvent {
        data class OnBottomNavigationItemClick(val innerNavHostController: NavHostController, val route: String) : UIEvent()
        object OnSignOut : UIEvent()
    }

    sealed class BaseEvent {
        object OnOpenQuickActionsBottomSheet : BaseEvent()
        object OnOpenMyProductsBottomSheet : BaseEvent()
        data class OnCallStartTimer(val timerInFuture: Long?) : BaseEvent()
    }
}
