package com.multimoney.multimoney.presentation.ui.login.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor() : BaseViewModel() {

    // Interactions
    var isSignInEnabled by mutableStateOf(false)

    // Fields
    var userEmail by mutableStateOf("")
    var userEmailError by mutableStateOf(Pair(false, R.string.error_empty))
    var userPassword by mutableStateOf("")
    var userPasswordError by mutableStateOf(Pair(false, R.string.error_empty))
    var userName by mutableStateOf<String?>(null)
    var isFingerprintChecked by mutableStateOf(false)
    var successMessage by mutableStateOf("")

    fun signIn() {
        isLoading = true
        userEmailError = Pair(false, R.string.error_empty)
        userPasswordError = Pair(false, R.string.sign_in_validation)

        Amplify.Auth.confirmSignUp("", "", {}, {})

        Amplify.Auth.signIn(userEmail, userPassword, {
            if (it.isSignInComplete) {
                Amplify.Auth.fetchAuthSession({ authSessionSuccess ->
                    val session = authSessionSuccess as AWSCognitoAuthSession
                    when (session.identityId.type) {
                        AuthSessionResult.Type.SUCCESS -> {
                            successMessage = session.identityId.value ?: ""
                            isLoading = false
                        }
                        AuthSessionResult.Type.FAILURE -> cognitoError()
                    }
                }, {
                    cognitoError()
                })
            } else {
                cognitoError()
            }
        }, {
            cognitoError()
        })
    }

    fun isFormValid() {
        userEmailError = Pair(false, R.string.error_empty)
        userPasswordError = Pair(false, R.string.error_empty)
        when {
            userEmail.isBlank() -> {
                isSignInEnabled = false
            }
            isEmailValid(userEmail).not() -> {
                userEmailError = Pair(true, R.string.sign_up_email_not_valid)
                isSignInEnabled = false
            }
            userPassword.isBlank() -> {
                isSignInEnabled = false
            }
            else -> {
                isSignInEnabled = true
            }
        }
    }


    private fun cognitoError() {
        userEmailError = Pair(true, R.string.error_empty)
        userPasswordError = Pair(true, R.string.sign_in_validation)
        isLoading = false
    }
}