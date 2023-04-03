package com.multimoney.multimoney.presentation.ui.splash

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryGetConfigurationVersionUseCase
import com.multimoney.domain.model.util.catalog.ConfigurationPlatform
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.BuildConfig
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreenViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.ui.splash.SplashScreenViewModel.UIEvent.OnCallQueryGetConfigurationVersion
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.getUserCountry
import com.multimoney.multimoney.util.firebase.FireBaseEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val queryGetConfigurationVersionUseCase: QueryGetConfigurationVersionUseCase
) : BaseViewModel(false) {

    private fun callQueryGetConfigurationVersion(context: Context) = executeUseCase {
        queryGetConfigurationVersionUseCase.invoke(
            platform = ConfigurationPlatform.Android.value,
            appVersion = BuildConfig.VERSION_NAME,
            idBrand = getIdBrand(context)
        ).collectLatest { result ->
            result.onSuccess {
                navigateToNextScreen()
            }
            result.onFailure {
                navigateToForceUpdateScreen(getIdBrand(context))
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

    private fun navigateToForceUpdateScreen(idBrand: Int) {
        registerSplashEvent()
        popAndNavigateTo(
            route = Screen.ForceUpdateScreen.baseRoute.plus(
                getNavParam(ID_BRAND, idBrand)
            ),
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

    private fun getIdBrand(context: Context): Int {
        var idBrand = Brand.CostaRica.id
        val countryCode = context.getUserCountry()
        if (countryCode.isNotEmpty()) {
            idBrand = Brand.Search.getIdBrandByCountryCode(countryCode)
        }
        return idBrand
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToNextScreen -> navigateToNextScreen()
            is OnCallQueryGetConfigurationVersion -> callQueryGetConfigurationVersion(event.context)
        }
    }

    sealed class UIEvent {
        object OnNavigateToNextScreen : UIEvent()
        data class OnCallQueryGetConfigurationVersion(val context: Context) : UIEvent()
    }
}
