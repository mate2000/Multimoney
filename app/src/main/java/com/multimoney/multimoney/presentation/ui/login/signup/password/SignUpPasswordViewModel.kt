package com.multimoney.multimoney.presentation.ui.login.signup.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.passwordHasALowercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasANumberValidation
import com.multimoney.multimoney.presentation.util.passwordHasAUppercaseLetterValidation
import com.multimoney.multimoney.presentation.util.sameConsecutiveCharacterValidationValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpPasswordViewModel @Inject constructor() : BaseViewModel() {

    var password by mutableStateOf("")
    var passwordError by mutableStateOf(Pair(false, R.string.error_empty))
    var confirmPassword by mutableStateOf("")
    var confirmPasswordError by mutableStateOf(Pair(false, R.string.error_empty))

    var eightCharactersMinimum by mutableStateOf<Boolean?>(null)
    var oneUppercaseError by mutableStateOf<Boolean?>(null)
    var oneLowercaseError by mutableStateOf<Boolean?>(null)
    var oneNumberError by mutableStateOf<Boolean?>(null)

    fun isFormValid() {
        oneLowercaseError?.not() ?: false && oneUppercaseError?.not() ?: false && oneNumberError?.not() ?: false &&
                confirmPasswordError.first.not()
    }

    fun validatePassword() {
        oneUppercaseError = !passwordHasAUppercaseLetterValidation(password)
        oneLowercaseError = !passwordHasALowercaseLetterValidation(password)
        oneNumberError = !passwordHasANumberValidation(password)

        if (sameConsecutiveCharacterValidationValidation(password)) {
            confirmPasswordError = Pair(true, R.string.sign_up_password_requirement_no_the_same_consecutive_character)
        }
    }

    fun validateConfirmPassword() {
        if (confirmPassword != password) {
            confirmPasswordError = Pair(true, R.string.sign_up_password_confirm_password_error)
        }
    }

    fun clearPassword() {
        passwordError = Pair(false, R.string.error_empty)
    }

    fun clearConfirmPassword() {
        confirmPasswordError = Pair(false, R.string.error_empty)
    }
}