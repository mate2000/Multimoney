package com.multimoney.multimoney.presentation.ui.login.signup.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.multimoney.data.util.catalog.Brand
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpPasswordViewModel @Inject constructor(
    private val queryValidationSecurityUseCase: QueryValidationSecurityUseCase
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

    fun signUp(
        password: String,
        email: String,
        identification: String,
        pkUser: String,
        status: String,
        onSuccess: () -> Unit
    ) {
        val attrs = mapOf(
            AuthUserAttributeKey.email() to email,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_IDENTIFICATION) to identification,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_PK_USER) to pkUser,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_STATUS) to status,
            AuthUserAttributeKey.custom(COGNITO_CUSTOM_ID_BRAND) to Brand.Revamp.id.toString()
        )
        val options = AuthSignUpOptions.builder()
            .userAttributes(attrs.map { AuthUserAttribute(it.key, it.value) })
            .build()
        Amplify.Auth.signUp(email, password, options, {
            onSuccess.invoke()
        }, {
            isLoading = false
            onFailure = DialogParameters(
                description = it.localizedMessage ?: "",
                isActive = mutableStateOf(true)
            )
        })
    }

    private fun resetValidationLabel(password: String) {
        if (password.isEmpty()) {
            eightCharactersMinimumState = null
            oneUppercaseState = null
            oneLowercaseState = null
            oneNumberState = null
        }
    }

    companion object {
        const val COGNITO_CUSTOM_IDENTIFICATION = "custom:Identification"
        const val COGNITO_CUSTOM_PK_USER = "custom:PkUser"
        const val COGNITO_CUSTOM_STATUS = "custom:Status"
        const val COGNITO_CUSTOM_ID_BRAND = "custom:IdBrand"
    }
}