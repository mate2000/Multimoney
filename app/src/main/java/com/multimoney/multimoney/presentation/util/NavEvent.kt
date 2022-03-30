package com.multimoney.multimoney.presentation.util

import androidx.annotation.StringRes

sealed class NavEvent {
    object PopBackStack : NavEvent()
    data class Navigate(val route: String) : NavEvent()
    data class PopAndNavigate(val route: String, val popTo: String) : NavEvent()
    data class ShowSnackbar(@StringRes val message: Int, @StringRes val action: Int? = null) :
        NavEvent()
}
