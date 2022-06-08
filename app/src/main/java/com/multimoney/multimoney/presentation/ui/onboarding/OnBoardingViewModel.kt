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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel() {

    var currentStep by mutableStateOf(1)
    var maxWidth = 0

    // Interactions
    var isPressed by mutableStateOf(false)

    // Fields
    var title by mutableStateOf(R.string.onboarding_step_one_title)
    var subtitle by mutableStateOf(R.string.onboarding_step_one_sub_title)
    var icon by mutableStateOf(R.drawable.ic_onboarding_step_one)

    val goToNextScreen = {
        if (currentStep < MAX_STEPS) {
            currentStep++
            val newValues = getStepContent(currentStep)
            title = newValues[STEP_TITLE]
            subtitle = newValues[STEP_SUBTITLE]
            icon = newValues[STEP_ICON]
        }
    }

    val goToPreviousScreen = {
        if (currentStep - 1 > 0) {
            currentStep--
            val newValues = getStepContent(currentStep)
            title = newValues[STEP_TITLE]
            subtitle = newValues[STEP_SUBTITLE]
            icon = newValues[STEP_ICON]
        }
    }

    fun onPress(pressGestureScope: PointerInputScope) {
        maxWidth = pressGestureScope.size.width
        viewModelScope.launch {
            pressGestureScope.detectTapGestures(
                onPress = {
                    val pressStartTime = System.currentTimeMillis()
                    isPressed = true
                    tryAwaitRelease()
                    val pressEndTime = System.currentTimeMillis()
                    val totalPressTime = pressEndTime - pressStartTime
                    if (totalPressTime < 300) {
                        val isTapOnRightThreeQuarters = (it.x > (maxWidth / 4))
                        if (isTapOnRightThreeQuarters) {
                            goToNextScreen()
                        } else {
                            goToPreviousScreen()
                        }
                    }
                    isPressed = false
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

    fun navigateToNextScreen(screen: String) {
        viewModelScope.launch {
            dataStorePreferences.isOnBoardingEnabled(false)
            popAndNavigateTo(
                route = screen,
                popTo = Screen.OnBoardingScreen.route
            )
        }
    }

    companion object {
        const val MAX_STEPS = 3
        const val STEP_ONE = 1
        const val STEP_TWO = 2
        const val STEP_TITLE = 0
        const val STEP_SUBTITLE = 1
        const val STEP_ICON = 2
    }
}