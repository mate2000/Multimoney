package com.multimoney.multimoney.presentation.ui.splash

import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreenViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.util.firebase.FireBaseEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    private fun navigateToNextScreen() {
        registerSplashEvent()
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

    private fun registerSplashEvent() {
        provideFireBaseEventHelper.logEvent(FireBaseEvents.Splash)
        viewModelScope.launch {
            if (dataStorePreferences.isAdjustSplashEventRegister().first()) {
                registerAdjustEvent(adjustEventType = AdjustEventType.SPLASH_1001, isLoggedIn = false)
                dataStorePreferences.isAdjustSplashEventRegister(false)
            }
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

    companion object {
        const val SPLASH_DURATION = 2000L
    }
}
