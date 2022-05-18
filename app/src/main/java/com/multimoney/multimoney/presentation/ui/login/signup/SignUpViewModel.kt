package com.multimoney.multimoney.presentation.ui.login.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val biometricHelper: BiometricHelper,
    val validationSecurityUseCase: QueryValidationSecurityUseCase
) : BaseViewModel() {

    var currentStep by mutableStateOf(STEP_ONE)

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

    fun nextStep() {
        if (currentStep < SIGN_UP_TOTAL_STEPS) {
            currentStep++
            isCloseVisible = currentStep > SIGN_UP_INITIAL_STEP
        } else {
            savaPassword()
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

    private fun completedProcessAction() = popAndNavigateTo(
        route = if (isBiometricAvailable) {
            "${Screen.SignUpBiometricsScreen.baseRoute}/$userEmail/$userPassword"
        } else {
            Screen.SignInScreen.route
        },
        popTo = Screen.SignUpScreen.route
    )

    private fun savaPassword() {
        viewModelScope.launch {
            validationSecurityUseCase.invoke(
                pkUser = 229913,
                password = userPassword,
                user = "ecruzCR",
                idBrand = 5
            ).collectLatest { result ->
                result.onSuccess {
                    isLoading = false
                    completedProcessAction()
                }
                result.onLoading {
                    isLoading = true
                }
                result.onFailure {
                    isLoading = false
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                }
            }
        }
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