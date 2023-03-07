package com.multimoney.multimoney.presentation.ui.login.registereduser.email

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.model.security.UserData
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_DATA
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnEmailValueChange
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnValidateEmail
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.isEmailValid
import com.multimoney.multimoney.presentation.util.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisteredUserEmailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences

) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var idBrand: Int = 0
    var userData: UserData? = null

    init {
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        userData = savedStateHandle.get<UserData>(USER_DATA)
    }

    private fun clearEmailError() {
        uiState = uiState.copy(emailError = Pair(false, R.string.error_empty))
    }

    private fun isFormValid() = when {
        uiState.email.isBlank() -> false
        isEmailValid(uiState.email).not() -> false
        else -> true
    }

    private fun onValidateEmail() {
        if (isEmailValid(uiState.email).not()) {
            uiState = uiState.copy(emailError = Pair(true, R.string.registered_user_email_not_valid))
        }
    }

    private fun onEmailValueChange(value: String) {
        uiState = uiState.copy(email = value)
        uiState = uiState.copy(isFormValid = isFormValid())
        clearEmailError()
    }

    private fun onBackClick() = navigateBack(
        popTo = Screen.SignUpScreen.route,
        isRestart = false
    )

    private fun onContinueClick() = if (uiState.email != (userData?.email ?: "")) {
        uiState = uiState.copy(emailError = Pair(true, R.string.registered_user_email_different))
    } else {
        viewModelScope.launch {
            if (dataStorePreferences.isAdjustSingUpAlreadyCustomerEmailEventRegister().first()) {
                registerAdjustEvent(AdjustEventType.SIGNUP_ALREADY_BEEN_CUSTOMERS_EMAIL_2009, isLoggedIn = false, data = userData?.toJson() ?: "")
                dataStorePreferences.isAdjustSingUpAlreadyCustomerEmailEventRegister(false)
            }
        }
        navigateTo(
            route = Screen.RegisteredUserOtpOptionsScreen.baseRoute
                .plus(
                    getNavParam(PREVIOUS_SCREEN, Screen.RegisteredUserEmailScreen.baseRoute)
                )
                .plus(
                    getNavParam(ID_BRAND, idBrand)
                )
                .plus(
                    getNavParam(USER_DATA, encodeData(userData))
                )
        )
    }

    data class UIState(
        // Fields
        val email: String = "",
        val emailError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_email_required),
        val isFormValid: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnEmailValueChange -> onEmailValueChange(event.value)
            is OnValidateEmail -> onValidateEmail()
            is OnBackClick -> onBackClick()
            is OnContinueClick -> onContinueClick()
        }
    }

    sealed class UIEvent {
        data class OnEmailValueChange(val value: String) : UIEvent()
        object OnValidateEmail : UIEvent()
        object OnBackClick : UIEvent()
        object OnContinueClick : UIEvent()
    }
}
