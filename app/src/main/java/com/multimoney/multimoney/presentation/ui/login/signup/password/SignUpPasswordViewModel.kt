package com.multimoney.multimoney.presentation.ui.login.signup.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.noMoreThanThreeLettersOrNumbers
import com.multimoney.multimoney.presentation.util.noMoreThanTwoConsecutiveLetterOrNumber
import com.multimoney.multimoney.presentation.util.noMoreThanTwoEqualConsecutiveLetterOrNumber
import com.multimoney.multimoney.presentation.util.passwordHasALowercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasANumberValidation
import com.multimoney.multimoney.presentation.util.passwordHasAUppercaseLetterValidation
import com.multimoney.multimoney.presentation.util.passwordHasMinimumCharacters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpPasswordViewModel @Inject constructor() : BaseViewModel() {

    var password by mutableStateOf("")
    var passwordError by mutableStateOf(Pair(false, R.string.error_empty))
    var confirmPassword by mutableStateOf("")
    var confirmPasswordError by mutableStateOf(Pair(false, R.string.error_empty))

    var eightCharactersMinimumState by mutableStateOf<Boolean?>(null)
    var oneUppercaseState by mutableStateOf<Boolean?>(null)
    var oneLowercaseState by mutableStateOf<Boolean?>(null)
    var oneNumberState by mutableStateOf<Boolean?>(null)

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
            noMoreThanTwoEqualConsecutiveLetterOrNumber(password) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_no_more_than_two_the_same_consecutive_character
                )
            }
            noMoreThanTwoConsecutiveLetterOrNumber(password) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_no_more_than_two_consecutive_character
                )
            }
            noMoreThanThreeLettersOrNumbers(password) -> {
                Pair(
                    true, R.string.sign_up_password_requirement_no_more_than_three_character
                )
            }
            else -> {
                Pair(false, R.string.error_empty)
            }
        }
    }

    fun validateConfirmPassword() {
        if (confirmPassword != password) {
            confirmPasswordError = Pair(true, R.string.sign_up_password_confirm_password_error)
        } else {
            Pair(false, R.string.error_empty)
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