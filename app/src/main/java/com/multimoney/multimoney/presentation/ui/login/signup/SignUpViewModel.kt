package com.multimoney.multimoney.presentation.ui.login.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : BaseViewModel() {

    var currentStep by mutableStateOf(1)

    // Interactions
    var isCloseVisible by mutableStateOf(false)
    var isContinueEnabled by mutableStateOf(false)

    // Fields
    var userEmail by mutableStateOf("")
    var userEmailError by mutableStateOf(Pair(false, R.string.sign_up_email_required))

    fun nextStep() {
        if (currentStep < SIGN_UP_TOTAL_STEPS && isFormStepValid()) {
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

    fun isFormStepValid(): Boolean = when (currentStep) {
        STEP_ONE -> isFormStepOneValid()
        else -> isFormStepOneValid()
    }

    private fun isFormStepOneValid(): Boolean {
        userEmailError = Pair(false, R.string.error_empty)
        when {
            userEmail.isBlank() -> {
                isContinueEnabled = false
            }
            isEmailValid(userEmail).not() -> {
                userEmailError = Pair(true, R.string.sign_up_email_not_valid)
                isContinueEnabled = false
            }
            else -> {
                isContinueEnabled = true
            }
        }

        return isContinueEnabled
    }

    companion object {
        const val SIGN_UP_TOTAL_STEPS = 5
        const val SIGN_UP_INITIAL_STEP = 1
        const val STEP_ONE = 1
        const val STEP_TWO = 2
        const val STEP_THREE = 3
        const val STEP_FOUR = 4
        const val STEP_FIVE = 5
    }
}