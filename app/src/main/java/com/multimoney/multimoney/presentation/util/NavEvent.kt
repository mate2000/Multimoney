package com.multimoney.multimoney.presentation.util

import androidx.annotation.StringRes
import androidx.navigation.NavHostController

sealed class NavEvent {
    data class PopBackStack(val popTo: String, val isRestart: Boolean) : NavEvent()
    data class Navigate(val route: String) : NavEvent()
    data class InnerNavigate(val innerNavigate: NavHostController, val route: String) : NavEvent()
    data class PopAndNavigate(val route: String, val popTo: String) : NavEvent()
    data class ShowSnackbar(@StringRes val message: Int, @StringRes val action: Int? = null) :
        NavEvent()
}
