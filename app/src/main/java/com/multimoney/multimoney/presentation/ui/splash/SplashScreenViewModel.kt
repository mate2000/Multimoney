package com.multimoney.multimoney.presentation.ui.splash

import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreenViewModel.UIEvent.OnNavigateToNextScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    private fun navigateToNextScreen() {
        viewModelScope.launch {
            popAndNavigateTo(
                route = if (dataStorePreferences.isOnBoardingEnabled().first()) {
                    Screen.OnBoardingScreen.route
                } else {
                    Screen.SignInScreen.route
                },
                popTo = Screen.SplashScreen.route
            )
        }
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToNextScreen -> navigateToNextScreen()
        }
    }

    sealed class UIEvent {
        object OnNavigateToNextScreen : UIEvent()
    }
}