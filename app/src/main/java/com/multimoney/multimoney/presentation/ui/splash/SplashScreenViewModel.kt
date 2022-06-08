package com.multimoney.multimoney.presentation.ui.splash

import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel() {

    fun navigateToNextScreen() {
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
}