package com.multimoney.multimoney.presentation.util

import androidx.annotation.StringRes

sealed class UiEvent {
    object PopBackStack : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data class PopAndNavigate(val route: String, val popTo: String) : UiEvent()
    data class ShowSnackbar(@StringRes val message: Int, @StringRes val action: Int? = null) :
        UiEvent()
}
