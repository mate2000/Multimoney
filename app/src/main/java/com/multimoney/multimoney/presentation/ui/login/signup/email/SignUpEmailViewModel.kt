package com.multimoney.multimoney.presentation.ui.login.signup.email

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.security.MutationUserValidationUseCase
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SignUpEmailViewModel @Inject constructor(
    private val mutationUserValidationUseCase: MutationUserValidationUseCase
) :
    BaseViewModel() {

    // Fields
    var userEmail by mutableStateOf("")
    var userEmailError by mutableStateOf(Pair(false, R.string.sign_up_email_required))

    // Interactions
    var onSuccessUserDataValidation by mutableStateOf<UserData?>(null)
    var onFailure by mutableStateOf(DialogParameters())
    var isFirstLaunch = true
    var userCompletedDialogDescription = ""

    fun isFormValid() = when {
        userEmail.isBlank() -> false
        isEmailValid(userEmail).not() -> false
        else -> true
    }

    fun isUserEmailValid() {
        if (isEmailValid(userEmail).not()) {
            userEmailError = Pair(true, R.string.sign_up_email_not_valid)
        }
    }

    fun clearUserEmailError() {
        userEmailError = Pair(false, R.string.error_empty)
    }

    fun isDataChanged() = onSuccessUserDataValidation?.email != userEmail

    fun callMutationUserValidationUseCase(email: String, currentStep: String, idBrand: Int) =
        executeUseCase {
            isLoading = true
            mutationUserValidationUseCase(
                email = email,
                currentStep = currentStep,
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess {
                    onSuccessUserDataValidation = it
                    isLoading = false
                }
                result.onFailure {
                    isLoading = false
                    onFailure = DialogParameters(
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