package com.multimoney.multimoney.presentation.ui.login.signup.biometrics.failure

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.util.BiometricHelper
import javax.inject.Inject

class SignUpBiometricsFailureViewModel @Inject constructor(
    val biometricHelper: BiometricHelper
) : BaseViewModel() {

    fun navigateToSignIn() {
        popAndNavigateTo(
            route = Screen.SignInScreen.route,
            popTo = Screen.SignUpScreen.route
        )
    }
}