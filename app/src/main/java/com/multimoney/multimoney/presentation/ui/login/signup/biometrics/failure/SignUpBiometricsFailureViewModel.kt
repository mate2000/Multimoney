package com.multimoney.multimoney.presentation.ui.login.signup.biometrics.failure

import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpBiometricsFailureViewModel @Inject constructor(
    val biometricHelper: BiometricHelper
) : BaseViewModel() {

    fun navigateToCompleted() {
        popAndNavigateTo(
            route = Screen.SignUpCompleted.route,
            popTo = Screen.SignUpCompleted.route
        )
    }
}