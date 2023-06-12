package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.email

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.FieldToChange
import com.multimoney.domain.interaction.security.QueryValidateUserExistsUseCase
import com.multimoney.domain.interaction.security.QueryValidateUserStatusUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.LAST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val queryValidateUserExistsUseCase: QueryValidateUserExistsUseCase,
    private val dataStorePreferences: DataStorePreferences,

    ) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    private var deviceId = ""
    private fun onStart() {
        uiState = uiState.copy(
            phoneNumber = savedStateHandle[PHONE_NUMBER],
            idBrand = savedStateHandle[ID_BRAND],
            email = savedStateHandle[EMAIL],
            userName = savedStateHandle[USER_NAME],
            identification = savedStateHandle[IDENTIFICATION],
            pkUser = savedStateHandle[PK_USER],
            firstName = savedStateHandle[FIRST_NAME],
            lastName = savedStateHandle[LAST_NAME],
            idClient = savedStateHandle.get<Int>(ID_CLIENT)?.toInt()
        )
        viewModelScope.launch {
            deviceId = dataStorePreferences.getDeviceId().first()
        }
    }

    private fun onContinueButtonClicked() {
        executeUseCase {
            queryValidateUserExistsUseCase.invoke(
                uiState.newEmail.orEmpty(),
                deviceId
            ).collectLatest {
                it.onSuccess { infoUser ->
                    uiState = uiState.copy(isLoading = false)
                    if (infoUser?.isNewUser == true) {
                        navigateToVerifyIdentityScreen()
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            userEmailError = Pair(
                                true,
                                R.string.empty
                            ),
                            duplicatedEmailErrorMessage = R.string.profile_email_already_registered_error
                        )
                    }

                }.onMessage {
                    uiState = uiState.copy(
                        isLoading = false,
                        userEmailError = Pair(
                            true,
                            R.string.empty
                        ),
                        duplicatedEmailErrorMessage = R.string.profile_email_already_registered_error
                    )
                }.onFailure {
                    uiState = uiState.copy(
                        isLoading = false,
                        isAlertResultVisible = true
                    )
                }.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    private fun navigateToVerifyIdentityScreen() {
        navigateTo("${Screen.ProfileVerifyIdentityEmailScreen.baseRoute}/${uiState.idClient}/${FieldToChange.EMAIL.value}/${uiState.idBrand}/${uiState.pkUser}/${uiState.phoneNumber}/${uiState.email}/${uiState.newEmail}/${uiState.identification}/${uiState.userName}/${uiState.firstName}/${uiState.lastName}")
    }

    private fun isFormValid() {
        uiState = when {
            uiState.newEmail?.isBlank() == true -> {
                uiState.copy(isButtonEnabled = false)
            }
            uiState.newEmailConfirmation?.isBlank() == true -> {
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
            uiState.newEmail.equals(uiState.email) or uiState.newEmailConfirmation.equals(uiState.email) -> {
                uiState.copy(
                    isButtonEnabled = false,
                    userEmailError = Pair(
                        true,
                        R.string.profile_email_not_equal_than_previous_error
                    )
                )
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
        uiState = uiState.copy(newEmail = newEmail, duplicatedEmailErrorMessage = R.string.empty)
        clearUserEmailError()
        isFormValid()
    }

    private fun onUserEmailConfirmationChange(newEmailConfirmation: String) {
        uiState = uiState.copy(
            newEmailConfirmation = newEmailConfirmation,
            duplicatedEmailErrorMessage = R.string.empty
        )
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
        val lastName: String? = null,
        val pkUser: String? = null,
        val idClient: Int? = null,
        val phoneCode: String = "",
        val isButtonEnabled: Boolean = false,
        val userEmailError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_email_required),
        val duplicatedEmailErrorMessage: Int = R.string.empty,
        val isLoading: Boolean = false,
        val isAlertResultVisible: Boolean = false,
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnUserEmailValueChange -> onUserEmailChange(event.newEmail)
            is UIEvent.OnUserEmailConfirmationValueChange -> onUserEmailConfirmationChange(
                event.newEmailConfirmation
            )
            is UIEvent.OnValidateUserEmail -> isUserEmailValid()
            is UIEvent.OnValidateUserEmailConfirmation -> isUserEmailConfirmationValid()
            is UIEvent.OnContinueButtonClicked -> onContinueButtonClicked()
            is UIEvent.OnNavigateBack -> navigateBack(
                Screen.ProfilePersonalInfoScreen.route,
                false
            )
            is UIEvent.OnStart -> onStart()
        }
    }

    sealed class UIEvent {
        data class OnUserEmailValueChange(val newEmail: String) : UIEvent()
        data class OnUserEmailConfirmationValueChange(val newEmailConfirmation: String) : UIEvent()
        object OnContinueButtonClicked : UIEvent()
        object OnValidateUserEmail : UIEvent()
        object OnValidateUserEmailConfirmation : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnStart : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}