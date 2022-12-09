package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.email

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.FieldToChange
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(
            phoneNumber = savedStateHandle[PHONE_NUMBER],
            idBrand = savedStateHandle[ID_BRAND],
            email = savedStateHandle[EMAIL],
            userName = savedStateHandle[USER_NAME],
            identification = savedStateHandle[IDENTIFICATION],
            pkUser = savedStateHandle[PK_USER],
            firstName = savedStateHandle[FIRST_NAME],
        )
    }


    private fun onContinueButtonClicked() {
        navigateTo("${Screen.ProfileVerifyIdentityEmailScreen.baseRoute}/${FieldToChange.EMAIL.value}/${uiState.idBrand}/${uiState.pkUser}/${uiState.phoneNumber}/${uiState.email}/${uiState.newEmail}/${uiState.identification}/${uiState.userName}/${uiState.firstName}")
    }

    private fun isFormValid() {
        uiState = when {
            uiState.newEmail?.isBlank() == true -> {
                uiState.copy(isButtonEnabled = false)
            }
            uiState . newEmailConfirmation ?. isBlank () == true -> {
                uiState.copy(isButtonEnabled = false)
            }
            isEmailValid(uiState.newEmail).not() -> {
                uiState.copy(isButtonEnabled = false)
            }
            isEmailValid(uiState.newEmailConfirmation).not() -> {
                uiState.copy(isButtonEnabled = false)
            }
            uiState.newEmail.equals(uiState.newEmailConfirmation).not() -> {
                uiState.copy(isButtonEnabled = false)
            }
            else -> {
                uiState.copy(isButtonEnabled = true)
            }
        }
    }

    private fun isUserEmailValid() {
        if (isEmailValid(uiState.newEmail).not()) {
            uiState =
                uiState.copy(userEmailError = Pair(true, R.string.profile_email_check_format_error))
        } else if (uiState.newEmail.equals(uiState.newEmailConfirmation).not()) {
            uiState = uiState.copy(
                userEmailError = Pair(
                    true,
                    R.string.profile_emails_does_not_match_error
                )
            )
        }
    }

    private fun isUserEmailConfirmationValid() {
        if (isEmailValid(uiState.newEmailConfirmation).not()) {
            uiState =
                uiState.copy(userEmailError = Pair(true, R.string.profile_email_check_format_error))
        } else if (uiState.newEmail.equals(uiState.newEmailConfirmation).not()) {
            uiState = uiState.copy(
                userEmailError = Pair(
                    true,
                    R.string.profile_emails_does_not_match_error
                )
            )
        }
    }

    private fun clearUserEmailError() {
        uiState = uiState.copy(userEmailError = Pair(false, R.string.error_empty))
    }

    private fun onUserEmailChange(newEmail: String) {
        uiState = uiState.copy(newEmail = newEmail)
        clearUserEmailError()
        isFormValid()
    }

    private fun onUserEmailConfirmationChange(newEmailConfirmation: String) {
        uiState = uiState.copy(newEmailConfirmation = newEmailConfirmation)
        clearUserEmailError()
        isFormValid()
    }

    data class UIState(
        // Fields
        val userName: String? = null,
        val email: String? = null,
        val newEmail: String? = null,
        val newEmailConfirmation: String? = null,
        val identification: String? = null,
        val phoneNumber: String? = null,
        val newPhoneNumber: String? = null,
        val idBrand: Int? = null,
        val firstName: String? = null,
        val pkUser: String? = null,
        val phoneCode: String = "",
        val isButtonEnabled: Boolean = false,
        val userEmailError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_email_required),
    )

    fun onUIEvent(event: ChangeEmailViewModel.UIEvent) {
        when (event) {
            is ChangeEmailViewModel.UIEvent.OnUserEmailValueChange -> onUserEmailChange(event.newEmail)
            is ChangeEmailViewModel.UIEvent.OnUserEmailConfirmationValueChange -> onUserEmailConfirmationChange(
                event.newEmailConfirmation
            )
            is ChangeEmailViewModel.UIEvent.OnValidateUserEmail -> isUserEmailValid()
            is ChangeEmailViewModel.UIEvent.OnValidateUserEmailConfirmation -> isUserEmailConfirmationValid()
            is ChangeEmailViewModel.UIEvent.OnContinueButtonClicked -> onContinueButtonClicked()
            is ChangeEmailViewModel.UIEvent.OnNavigateBack -> navigateBack(
                Screen.HomeScreen.route,
                false
            )
        }
    }

    sealed class UIEvent {
        data class OnUserEmailValueChange(val newEmail: String) : UIEvent()
        data class OnUserEmailConfirmationValueChange(val newEmailConfirmation: String) : UIEvent()
        object OnContinueButtonClicked : UIEvent()
        object OnValidateUserEmail : UIEvent()
        object OnValidateUserEmailConfirmation : UIEvent()
        object OnNavigateBack : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}
