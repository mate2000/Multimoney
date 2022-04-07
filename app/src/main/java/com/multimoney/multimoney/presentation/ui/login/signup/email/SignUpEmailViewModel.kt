package com.multimoney.multimoney.presentation.ui.login.signup.email

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpEmailViewModel @Inject constructor() : BaseViewModel() {

    // Fields
    var userEmail by mutableStateOf("")
    var userEmailError by mutableStateOf(Pair(false, R.string.sign_up_email_required))

    fun isFormValid(): Boolean {
        userEmailError = Pair(false, R.string.error_empty)
        return when {
            userEmail.isBlank() -> {
                false
            }
            isEmailValid(userEmail).not() -> {
                userEmailError = Pair(true, R.string.sign_up_email_not_valid)
                false
            }
            else -> {
                true
            }
        }
    }
}