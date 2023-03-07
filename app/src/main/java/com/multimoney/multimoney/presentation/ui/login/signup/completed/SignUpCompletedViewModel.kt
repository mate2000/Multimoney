package com.multimoney.multimoney.presentation.ui.login.signup.completed

import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.login.signup.completed.SignUpCompletedViewModel.UIEvent.OnNavigateToSignIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpCompletedViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    private fun onNavigateToSignIn() {
        restartSignUpPreferences()
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignUpCompleted.route
        )
    }

    private fun restartSignUpPreferences() {
        viewModelScope.launch {
            dataStorePreferences.isAdjustSingUpButtonClickedEventRegister(true)
            dataStorePreferences.isAdjustSingUp1EventRegister(true)
            dataStorePreferences.isAdjustSingUp2EventRegister(true)
            dataStorePreferences.isAdjustSingUp3EventRegister(true)
            dataStorePreferences.isAdjustSingUp4EventRegister(true)
            dataStorePreferences.isAdjustSingUp5EventRegister(true)
            dataStorePreferences.isAdjustSingUpAlreadyCustomerEmailEventRegister(true)
            dataStorePreferences.isAdjustSingUpAlreadyCustomerOTPEventRegister(true)
            dataStorePreferences.isAdjustSingUpAlreadyCustomerPasswordEventRegister(true)
        }
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToSignIn -> onNavigateToSignIn()
        }
    }

    sealed class UIEvent {
        object OnNavigateToSignIn : UIEvent()
    }
}
