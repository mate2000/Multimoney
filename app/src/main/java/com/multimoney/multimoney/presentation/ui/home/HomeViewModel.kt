package com.multimoney.multimoney.presentation.ui.home

import androidx.navigation.NavHostController
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel.UIEvent.OnBottomNavigationItemClick
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel() {

    private fun navigate(route: String) {
        navigateTo(route)
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnBottomNavigationItemClick -> innerNavigateTo(uiEvent.innerNavHostController, uiEvent.route)
        }
    }

    sealed class UIEvent {
        data class OnBottomNavigationItemClick(val innerNavHostController: NavHostController, val route: String) : UIEvent()
    }
}