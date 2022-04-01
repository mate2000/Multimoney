package com.multimoney.multimoney.presentation.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {

    var userEmail by mutableStateOf("")
    var userPassword by mutableStateOf("")
    var userPasswordError by mutableStateOf(Pair(false, 0))
    var userName by mutableStateOf<String?>(null)
    var isFingerprintChecked by mutableStateOf(false)
    var successMessage by mutableStateOf("")

    fun signUp() {
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), userEmail)
            .build()
        Amplify.Auth.signUp(userEmail, userPassword, options, {
            if (it.isSignUpComplete) {
                Log.i("AuthQuickstart", "Sign in succeeded")
            } else {
                Log.e("AuthQuickstart", "Sign in not complete")
            }
        }, {
            Log.e("AuthQuickStart", "Sign up failed", it.cause)
        })

    }

    fun confirmCode() {
        val options = AuthSignOutOptions.builder()
            .globalSignOut(true)
            .build()
        Amplify.Auth.signOut(options,
            {
                Log.i("AuthQuickstart", "Signed out globally")
            },
            {
                Log.e("AuthQuickstart", "Sign out failed", it)
            }
        )
    }

    fun logIn() {
        isLoading = true
        userPasswordError = Pair(false, R.string.login_validation)
        Amplify.Auth.signIn(userEmail, userPassword, {
            if (it.isSignInComplete) {
                Amplify.Auth.fetchAuthSession({ authSessionSuccess ->
                    val session = authSessionSuccess as AWSCognitoAuthSession
                    when (session.identityId.type) {
                        AuthSessionResult.Type.SUCCESS ->
                            successMessage = session.identityId.value ?: ""
                        AuthSessionResult.Type.FAILURE ->
                            userPasswordError = Pair(true, R.string.login_validation)
                    }
                    isLoading = false
                }, {
                    userPasswordError = Pair(true, R.string.login_validation)
                    isLoading = false
                })
            } else {
                userPasswordError = Pair(true, R.string.login_validation)
                isLoading = false
            }
        }, {
            userPasswordError = Pair(true, R.string.login_validation)
            isLoading = false
        })

    }
}