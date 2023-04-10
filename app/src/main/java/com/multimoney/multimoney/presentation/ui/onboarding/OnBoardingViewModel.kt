package com.multimoney.multimoney.presentation.ui.onboarding

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnGoToNextScreen
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnInitializeResources
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnPress
import com.multimoney.multimoney.presentation.util.SIM_CODE_EL_SALVADOR
import com.multimoney.multimoney.presentation.util.SIM_CODE_GUATEMALA
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.getUserCountry
import com.multimoney.multimoney.util.firebase.FireBaseEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var currentStep = STEP_ONE
    private var maxWidth = 0

    private fun goToNextScreen(context: Context) {
        if (currentStep < MAX_STEPS) {
            currentStep++
            val newValues = getStepContent(currentStep, context)
            uiState = uiState.copy(
                title = newValues[STEP_TITLE],
                subtitle = newValues[STEP_SUBTITLE],
                icon = newValues[STEP_ICON]
            )
        }
    }

    private fun goToPreviousScreen(context: Context) {
        if (currentStep - 1 > 0) {
            currentStep--
            val newValues = getStepContent(currentStep, context)
            uiState = uiState.copy(
                title = newValues[STEP_TITLE],
                subtitle = newValues[STEP_SUBTITLE],
                icon = newValues[STEP_ICON]
            )
        }
    }

    private fun initializeResources(context: Context) {
        val newValues = getStepContent(currentStep, context)
        uiState = uiState.copy(
            title = newValues[STEP_TITLE],
            subtitle = newValues[STEP_SUBTITLE],
            icon = newValues[STEP_ICON]
        )
    }

    private fun onPress(pressGestureScope: PointerInputScope, context: Context) {
        maxWidth = pressGestureScope.size.width
        viewModelScope.launch {
            pressGestureScope.detectTapGestures(
                onPress = {
                    val pressStartTime = System.currentTimeMillis()
                    uiState = uiState.copy(isPressed = true)
                    tryAwaitRelease()
                    val pressEndTime = System.currentTimeMillis()
                    val totalPressTime = pressEndTime - pressStartTime
                    if (totalPressTime < TOTAL_PRESS_TIME) {
                        val isTapOnRightThreeQuarters = (it.x > (maxWidth / QUARTER))
                        if (isTapOnRightThreeQuarters) {
                            goToNextScreen(context)
                        } else {
                            goToPreviousScreen(context)
                        }
                    }
                    uiState = uiState.copy(isPressed = false)
                }
            )
        }
    }

    private fun getCountryCode(context: Context) = context.getUserCountry()

    private fun getStepContent(step: Int, context: Context): List<Int> = when (step) {
        STEP_ONE -> {
            provideFireBaseEventHelper.logEvent(FireBaseEvents.OnboardingOne)
            registerAdjustEvent(AdjustEventType.ON_BOARDING_1_1002, isLoggedIn = false)
            when (getCountryCode(context)) {
                SIM_CODE_EL_SALVADOR -> listOf(
                    R.string.onboarding_el_salvador_step_one_title,
                    R.string.onboarding_el_salvador_step_one_sub_title,
                    R.drawable.ic_onboarding_step_one
                )
                SIM_CODE_GUATEMALA -> listOf(
                    R.string.onboarding_guatemala_step_one_title,
                    R.string.onboarding_guatemala_step_one_sub_title,
                    R.drawable.ic_onboarding_step_one
                )
                else -> listOf(
                    R.string.onboarding_costa_rica_step_one_title,
                    R.string.onboarding_costa_rica_step_one_sub_title,
                    R.drawable.ic_onboarding_step_one
                )
            }
        }
        STEP_TWO -> {
            provideFireBaseEventHelper.logEvent(FireBaseEvents.OnboardingTwo)
            registerAdjustEvent(AdjustEventType.ON_BOARDING_2_1003, isLoggedIn = false)
            when (getCountryCode(context)) {
                SIM_CODE_EL_SALVADOR -> listOf(
                    R.string.onboarding_el_salvador_step_two_title,
                    R.string.onboarding_el_salvador_step_two_sub_title,
                    R.drawable.ic_onboarding_step_two
                )
                SIM_CODE_GUATEMALA -> listOf(
                    R.string.onboarding_guatemala_step_two_title,
                    R.string.onboarding_guatemala_step_two_sub_title,
                    R.drawable.ic_onboarding_step_two
                )
                else -> listOf(
                    R.string.onboarding_costa_rica_step_two_title,
                    R.string.onboarding_costa_rica_step_two_sub_title,
                    R.drawable.ic_onboarding_step_two
                )
            }
        }
        else -> {
            provideFireBaseEventHelper.logEvent(FireBaseEvents.OnboardingThree)
            registerAdjustEvent(AdjustEventType.ON_BOARDING_3_1004, isLoggedIn = false)
            when (getCountryCode(context)) {
                SIM_CODE_GUATEMALA -> listOf(
                    R.string.onboarding_guatemala_step_three_title,
                    R.string.onboarding_guatemala_step_three_sub_title,
                    R.drawable.ic_onboarding_step_three
                )
                SIM_CODE_EL_SALVADOR -> listOf(
                    R.string.onboarding_el_salvador_step_three_title,
                    R.string.onboarding_el_salvador_step_three_sub_title,
                    R.drawable.ic_onboarding_step_three
                )
                else -> listOf(
                    R.string.onboarding_costa_rica_step_three_title,
                    R.string.onboarding_costa_rica_step_three_sub_title,
                    R.drawable.ic_onboarding_step_three
                )
            }
        }
    }

    private fun navigateToNextScreen(screen: String, context: Context) {
        viewModelScope.launch {
            dataStorePreferences.isOnBoardingEnabled(false)
            if (dataStorePreferences.isAdjustSingUpButtonClickedEventRegister().first()) {
                registerAdjustEvent(
                    adjustEventType = AdjustEventType.SIGNUP_FIRST_BUTTON_CLICKED_2000,
                    isLoggedIn = false
                )
                dataStorePreferences.isAdjustSingUpButtonClickedEventRegister(false)
            }

            popAndNavigateTo(
                route = if (screen == Screen.SignUpScreen.baseRoute) {
                    "$screen/".plus(0).plus(
                        getNavParam(
                            ID_BRAND,
                            Brand.Search.getIdBrandByCountryCode(getCountryCode(context))
                        )
                    )
                } else {
                    screen
                },
                popTo = Screen.OnBoardingScreen.route
            )
        }
    }

    data class UIState(
        // Fields
        val title: Int = R.string.onboarding_el_salvador_step_one_title,
        val subtitle: Int = R.string.onboarding_el_salvador_step_one_sub_title,
        val icon: Int = R.drawable.ic_onboarding_step_one,

        // Interactions
        val isPressed: Boolean = false
    )

    fun onUIEvent(event: UIEvent, context: Context) {
        when (event) {
            is OnNavigateToNextScreen -> navigateToNextScreen(event.screen, context)
            is OnGoToNextScreen -> goToNextScreen(context)
            is OnPress -> onPress(event.pressGestureScope, context)
            is OnInitializeResources -> initializeResources(context)
        }
    }

    sealed class UIEvent {
        data class OnNavigateToNextScreen(
            val screen: String,
            val context: Context
        ) : UIEvent()
        data class OnPress(val pressGestureScope: PointerInputScope) : UIEvent()
        object OnGoToNextScreen : UIEvent()
        object OnInitializeResources : UIEvent()
    }

    companion object {
        const val MAX_STEPS = 3
        const val STEP_ONE = 1
        const val STEP_TWO = 2
        const val STEP_TITLE = 0
        const val STEP_SUBTITLE = 1
        const val STEP_ICON = 2
        const val TOTAL_PRESS_TIME = 300
        const val QUARTER = 4
    }
}
