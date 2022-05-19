package com.multimoney.multimoney.presentation.ui.login.signup.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.domain.interaction.security.QueryValidationSecurityUseCase
import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.noMoreThanThreeConsecutiveLetterOrNumber
import com.multimoney.multimoney.presentation.util.noMoreThanThreeEqualConsecutiveLetterOrNumber
import com.multimoney.multimoney.presentation.util.noMoreThanThreeLettersOrNumbers
import com.multimoney.multimoney.presentation.util.passwordHasALowercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasANumberValidation
import com.multimoney.multimoney.presentation.util.passwordHasAUppercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasMinimumCharacters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpPasswordViewModel @Inject constructor(
    val queryValidationSecurityUseCase: QueryValidationSecurityUseCase
) : BaseViewModel() {

    //Fields
    var password by mutableStateOf("")
    var passwordError by mutableStateOf(Pair(false, R.string.error_empty))
    var confirmPassword by mutableStateOf("")
    var confirmPasswordError by mutableStateOf(Pair(false, R.string.error_empty))
    var eightCharactersMinimumState by mutableStateOf<Boolean?>(null)
    var oneUppercaseState by mutableStateOf<Boolean?>(null)
    var oneLowercaseState by mutableStateOf<Boolean?>(null)
    var oneNumberState by mutableStateOf<Boolean?>(null)

    //Interactions
    var onSuccessValidationSecurity by mutableStateOf<ValidateSecurity?>(null)
    var onFailure by mutableStateOf(DialogParameters())
    var isFirstLaunch = true

    fun isFormValid(): Boolean {
        return oneLowercaseState ?: false && oneUppercaseState ?: false && oneNumberState ?: false &&
                passwordHasMinimumCharacters(password) && (confirmPassword == password) && !confirmPasswordError.first
    }

    fun validatePassword() {
        eightCharactersMinimumState = passwordHasMinimumCharacters(password)
        oneUppercaseState = passwordHasAUppercaseLetterValidation(password) && password.isNotEmpty()
        oneLowercaseState = passwordHasALowercaseLetterValidation(password) && password.isNotEmpty()
        oneNumberState = passwordHasANumberValidation(password) && password.isNotEmpty()
        validateHasTheSameConsecutiveCharacter()
        resetValidationLabel(password)
    }

    private fun validateHasTheSameConsecutiveCharacter() {
        confirmPasswordError = when {
            noMoreThanThreeEqualConsecutiveLetterOrNumber(password) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            noMoreThanThreeConsecutiveLetterOrNumber(password) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            noMoreThanThreeLettersOrNumbers(password) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }
            (password.isNotEmpty() && confirmPassword.isNotEmpty() && confirmPassword != password) -> {
                Pair(true, R.string.sign_up_password_confirm_password_error)
            }
            else -> {
                Pair(false, R.string.error_empty)
            }
        }
    }

    fun isDataChanged() = onSuccessValidationSecurity?.status == null

    fun callQuerySavePassword(pkUser: String, user: String, idBrant: Int) {
        viewModelScope.launch {
            queryValidationSecurityUseCase.invoke(
                pkUser = pkUser,
                password = password,
                user = user,
                idBrand = idBrant
            ).collectLatest { result ->
                result.onSuccess {
                    onSuccessValidationSecurity = it
                    isLoading = false
                }
                result.onLoading {
                    isLoading = true
                }
                result.onFailure {
                    isLoading = false
                    onFailure = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                }
            }
        }
    }

    private fun resetValidationLabel(password: String) {
        if (password.isEmpty()) {
            eightCharactersMinimumState = null
            oneUppercaseState = null
            oneLowercaseState = null
            oneNumberState = null
        }
    }
}