package com.multimoney.multimoney.presentation.ui.login.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.GsonHelper
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.interaction.security.MutationUpdateUserRegisterUseCase
import com.multimoney.domain.model.security.ContactMeans
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.onfido.OnFidoHelper
import com.multimoney.multimoney.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val biometricHelper: BiometricHelper,
    val gsonHelper: GsonHelper,
    private val mutationUpdateUserRegisterUseCase: MutationUpdateUserRegisterUseCase,
    val onFidoHelper: OnFidoHelper
) : BaseViewModel() {

    var currentStep by mutableStateOf(SignUpStep.One.id)

    // Data
    var userData: UserData? = null
    var contactMeans: ContactMeans = ContactMeans()

    // Step three data
    var countryCode = ""

    // Step five data
    var userPassword = ""
    var isBiometricAvailable = false

    // Interactions
    var isCloseVisible by mutableStateOf(false)
    var isContinueEnabled by mutableStateOf(false)
    var nextAction: () -> Unit = {}

    fun nextStep() {
        if (currentStep <= SIGN_UP_TOTAL_STEPS) {
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
            "${Screen.SignUpBiometricsScreen.baseRoute}/${userData?.email}/$userPassword"
        } else {
            Screen.SignUpCompleted.route
        },
        popTo = Screen.SignUpScreen.route
    )


    fun callMutationUpdateUserRegisterUseCase() {
        viewModelScope.launch {
            mutationUpdateUserRegisterUseCase.invoke(
                pkUser = userData?.pkUser ?: "",
                user = userData?.userName ?: "",
                email = userData?.email ?: "",
                phoneNumber = userData?.phoneNumber,
                fullName = userData?.fullName,
                firstName = userData?.firstName,
                secondName = userData?.secondName,
                lastName = userData?.lastName,
                secondLastName = userData?.secondLastName,
                contactMeans = userData?.contactMeans,
                nationality = userData?.nationality,
                identification = userData?.identification,
                countryCode = userData?.countryCode,
                currentStep = userData?.currentStep ?: "",
                idBrand = Brand.Revamp.id
            ).collectLatest { result ->
                result.onSuccess {
                    isLoading = false
                    userData = it
                    nextStep()
                }
                result.onFailure {
                    isLoading = false
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                }
                result.onLoading {
                    isLoading = true
                }
            }
        }
    }

    companion object {
        const val SIGN_UP_TOTAL_STEPS = 5
    }
}