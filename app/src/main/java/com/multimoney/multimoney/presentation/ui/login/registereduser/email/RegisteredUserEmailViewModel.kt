package com.multimoney.multimoney.presentation.ui.login.registereduser.email

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.model.security.UserData
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_DATA
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnBackClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnContinueClick
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnEmailValueChange
import com.multimoney.multimoney.presentation.ui.login.registereduser.email.RegisteredUserEmailViewModel.UIEvent.OnValidateEmail
import com.multimoney.multimoney.presentation.util.getNavParam
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisteredUserEmailViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var previousScreen: String = ""
    private var idBrand: Int = 0
    private var userData: UserData? = null

    init {
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
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
        uiState = uiState.copy(email = value, isFormValid = isFormValid())
        clearEmailError()
    }

    private fun onBackClick() = navigateBack(popTo = previousScreen, isRestart = false)

    private fun onContinueClick() = if (uiState.email != (userData?.email ?: "")) {
        uiState = uiState.copy(emailError = Pair(true, R.string.registered_user_email_different))
    } else {
        navigateTo(
            route = Screen.RegisteredUserOtpScreen.baseRoute
                .plus(
                    getNavParam(PREVIOUS_SCREEN, previousScreen)
                )
                .plus(
                    getNavParam(ID_BRAND, idBrand)
                )
                .plus(
                    getNavParam(USER_DATA, userData)
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
