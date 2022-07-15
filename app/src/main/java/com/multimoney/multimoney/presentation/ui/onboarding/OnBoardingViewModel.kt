package com.multimoney.multimoney.presentation.ui.onboarding

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnGoToNextScreen
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnNavigateToNextScreen
import com.multimoney.multimoney.presentation.ui.onboarding.OnBoardingViewModel.UIEvent.OnPress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel() {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var currentStep = STEP_ONE
    private var maxWidth = 0

    private fun goToNextScreen() {
        if (currentStep < MAX_STEPS) {
            currentStep++
            val newValues = getStepContent(currentStep)
            uiState = uiState.copy(
                title = newValues[STEP_TITLE],
                subtitle = newValues[STEP_SUBTITLE],
                icon = newValues[STEP_ICON]
            )
        }
    }

    private fun goToPreviousScreen() {
        if (currentStep - 1 > 0) {
            currentStep--
            val newValues = getStepContent(currentStep)
            uiState = uiState.copy(
                title = newValues[STEP_TITLE],
                subtitle = newValues[STEP_SUBTITLE],
                icon = newValues[STEP_ICON]
            )
        }
    }

    private fun onPress(pressGestureScope: PointerInputScope) {
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
                            goToNextScreen()
                        } else {
                            goToPreviousScreen()
                        }
                    }
                    uiState = uiState.copy(isPressed = false)
                }
            )
        }
    }

    private fun getStepContent(step: Int): List<Int> = when (step) {
        STEP_ONE -> {
            listOf(
                R.string.onboarding_step_one_title,
                R.string.onboarding_step_one_sub_title,
                R.drawable.ic_onboarding_step_one
            )
        }
        STEP_TWO -> {
            listOf(
                R.string.onboarding_step_two_title,
                R.string.onboarding_step_two_sub_title,
                R.drawable.ic_onboarding_step_two
            )
        }
        else -> {
            listOf(
                R.string.onboarding_step_three_title,
                R.string.onboarding_step_three_sub_title,
                R.drawable.ic_onboarding_step_three
            )
        }
    }

    private fun navigateToNextScreen(screen: String) {
        viewModelScope.launch {
            dataStorePreferences.isOnBoardingEnabled(false)
            popAndNavigateTo(
                route = if (screen == Screen.SignUpScreen.baseRoute) {
                    "$screen/".plus(0)
                } else {
                    screen
                },
                popTo = Screen.OnBoardingScreen.route
            )
        }
    }

    data class UIState(
        // Fields
        val title: Int = R.string.onboarding_step_one_title,
        val subtitle: Int = R.string.onboarding_step_one_sub_title,
        val icon: Int = R.drawable.ic_onboarding_step_one,

        // Interactions
        val isPressed: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnNavigateToNextScreen -> navigateToNextScreen(event.screen)
            is OnGoToNextScreen -> goToNextScreen()
            is OnPress -> onPress(event.pressGestureScope)
        }
    }

    sealed class UIEvent {
        data class OnNavigateToNextScreen(val screen: String) : UIEvent()
        data class OnPress(val pressGestureScope: PointerInputScope) : UIEvent()
        object OnGoToNextScreen : UIEvent()
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