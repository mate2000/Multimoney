package com.multimoney.multimoney.presentation.ui.login.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.model.security.User
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val biometricHelper: BiometricHelper) : BaseViewModel() {

    var currentStep by mutableStateOf(SignUpStep.One.id)

    // Data
    var user: User? = null

    // Step one data
    var userEmail = ""

    // Step three data
    var countryCode = ""
    var phoneCode = ""
    var phoneNumber = ""
    var whatsapp = true
    var call = true

    // Step five data
    var userPassword = ""
    var isBiometricAvailable = false

    // Interactions
    var isCloseVisible by mutableStateOf(false)
    var isContinueEnabled by mutableStateOf(false)
    var nextAction: () -> Unit = {}

    fun nextStep() {
        if (currentStep < SIGN_UP_TOTAL_STEPS) {
            currentStep++
            isCloseVisible = currentStep > SignUpStep.One.id
        } else {
            completedProcessAction()
        }
    }

    fun previousStep() {
        if (currentStep > SignUpStep.One.id) {
            currentStep--
            isCloseVisible = currentStep > SignUpStep.One.id
        } else {
            popAndNavigateTo(
                route = Screen.SignInScreen.route,
                popTo = Screen.SignUpScreen.route
            )
        }
    }

    private fun completedProcessAction() = popAndNavigateTo(
        route = if (isBiometricAvailable) {
            "${Screen.SignUpBiometricsScreen.baseRoute}/$userEmail/$userPassword"
        } else {
            Screen.SignInScreen.route
        },
        popTo = Screen.SignUpScreen.route
    )


    companion object {
        const val SIGN_UP_TOTAL_STEPS = 5
    }
}