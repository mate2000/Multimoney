package com.multimoney.multimoney.presentation.ui.splash

import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.security.QueryGetConfigurationVersionUseCase
import com.multimoney.domain.model.util.catalog.ConfigurationPlatform
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.BuildConfig
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreenViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreenViewModel.UIEvent.OnCallQueryGetConfigurationVersion
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.util.firebase.FireBaseEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val queryGetConfigurationVersionUseCase: QueryGetConfigurationVersionUseCase
) : BaseViewModel(false) {

    private fun callQueryGetConfigurationVersion() = executeUseCase {
        queryGetConfigurationVersionUseCase.invoke(
            platform = ConfigurationPlatform.Android.value,
            appVersion = BuildConfig.VERSION_NAME,
            idBrand = if (dataStorePreferences.getIdBrand().firstOrNull()?.isNotEmpty() == true) {
                dataStorePreferences.getIdBrand().first().toInt()
            } else {
                0
            }
        ).collectLatest { result ->
            result.onSuccess {
                navigateToNextScreen()
            }
            result.onFailure {
                navigateToForceUpdateScreen()
            }
            result.onLoading {

            }
        }
    }

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

    private fun navigateToForceUpdateScreen() {
        registerSplashEvent()
        popAndNavigateTo(
            route = Screen.ForceUpdateScreen.route,
            popTo = Screen.SplashScreen.route
        )
    }

    private fun registerSplashEvent() {
        provideFireBaseEventHelper.logEvent(FireBaseEvents.Splash)
        viewModelScope.launch {
            if (dataStorePreferences.isAdjustSplashEventRegister().first()) {
                registerAdjustEvent(
                    adjustEventType = AdjustEventType.SPLASH_1001,
                    isLoggedIn = false
                )
                dataStorePreferences.isAdjustSplashEventRegister(false)
            }
        }
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToNextScreen -> navigateToNextScreen()
            is OnCallQueryGetConfigurationVersion -> callQueryGetConfigurationVersion()
        }
    }

    sealed class UIEvent {
        object OnNavigateToNextScreen : UIEvent()
        object OnCallQueryGetConfigurationVersion : UIEvent()
    }

    companion object {
        const val SPLASH_DURATION = 2000L
    }
}
