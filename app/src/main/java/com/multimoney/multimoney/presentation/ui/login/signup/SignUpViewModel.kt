package com.multimoney.multimoney.presentation.ui.login.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : BaseViewModel() {

    var currentStep by mutableStateOf(1)

    // Interactions
    var isCloseVisible by mutableStateOf(false)
    var isContinueEnabled by mutableStateOf(false)

    fun nextStep() {
        if (currentStep < SIGN_UP_TOTAL_STEPS) {
            currentStep++
            isCloseVisible = currentStep > SIGN_UP_INITIAL_STEP
        }
    }

    fun previousStep() {
        if (currentStep > SIGN_UP_INITIAL_STEP) {
            currentStep--
            isCloseVisible = currentStep > SIGN_UP_INITIAL_STEP
        } else {
            popAndNavigateTo(
                route = Screen.SignInScreen.route,
                popTo = Screen.SignUpScreen.route
            )
        }
    }

    companion object {
        const val SIGN_UP_TOTAL_STEPS = 5
        const val SIGN_UP_INITIAL_STEP = 1
        const val STEP_ONE = 1
        const val STEP_TWO = 2
        const val STEP_THREE = 3
        const val STEP_FOUR = 4
    }
}