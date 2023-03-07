package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.verifyidentity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.FieldToChange
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CHANGING_FIELD
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.NEW_EMAIL
import com.multimoney.multimoney.presentation.navigation.NEW_PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER
import com.multimoney.multimoney.presentation.navigation.PHONE_NUMBER_CODE
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.FIRST_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerifyIdentityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    init {
        uiState = uiState.copy(
            idBrand = savedStateHandle[ID_BRAND],
            phoneNumber = savedStateHandle[PHONE_NUMBER],
            newPhoneNumber = savedStateHandle[NEW_PHONE_NUMBER],
            newPhoneNumberCode = savedStateHandle[PHONE_NUMBER_CODE],
            identification = savedStateHandle[IDENTIFICATION],
            userName = savedStateHandle[USER_NAME],
            firstName = savedStateHandle[FIRST_NAME],
            email = savedStateHandle[EMAIL],
            pkUser = savedStateHandle[PK_USER],
            idClient = savedStateHandle[ID_CLIENT],
            changingField = savedStateHandle[CHANGING_FIELD],
            newEmail = savedStateHandle[NEW_EMAIL],
        )
        getTextResources()
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = when (uiState.idBrand) {
                Brand.CostaRica.id -> R.string.profile_where_do_you_want_to_receive_the_code
                else -> R.string.profile_where_do_you_want_to_receive_the_code_sv

            }
        )
    }

    private fun validateForm() {
        uiState = if (uiState.questionTwoValue || uiState.questionOneValue)
            uiState.copy(isButtonEnabled = true)
        else
            uiState.copy(isButtonEnabled = false)
    }

    private fun onQuestionOneValueChange(value: Boolean) {
        if (value != uiState.questionOneValue) {
            uiState = uiState.copy(questionOneValue = value, questionTwoValue = !value)
        }
        validateForm()
    }

    private fun onQuestionTwoValueChange(value: Boolean) {
        if (value != uiState.questionTwoValue) {
            uiState = uiState.copy(questionTwoValue = value, questionOneValue = !value)
        }
        validateForm()
    }

    private fun onContinueButtonClicked() {
        val sendMethod = when (uiState.changingField) {
            FieldToChange.PHONE.value -> if (uiState.questionOneValue) SEND_PHONE_METHOD else SEND_EMAIL_METHOD
            else -> if (uiState.questionOneValue) SEND_EMAIL_METHOD else SEND_PHONE_METHOD
        }
        val newValue = when (uiState.changingField) {
            FieldToChange.PHONE.value -> uiState.phoneCode.plus(uiState.newPhoneNumber)
            else -> uiState.newEmail
        }
        navigateTo("${Screen.ProfileValidateOTPScreen.baseRoute}/${uiState.idClient}/${uiState.changingField}/${newValue}/${sendMethod}/${uiState.identification}/${uiState.firstName}/${uiState.email}/${uiState.phoneNumber}/${uiState.pkUser}/${uiState.idBrand}/${uiState.userName}/${uiState.newPhoneNumberCode}")
    }

    data class UIState(
        // Fields
        val userName: String? = null,
        val phoneNumber: String? = null,
        val newPhoneNumber: String? = null,
        val newEmail: String? = null,
        val identification: String? = null,
        val idBrand: Int? = null,
        val phoneCode: String = "",
        val email: String? = null,
        val idClient: Int? = null,
        val countryCode: String? = null,
        val isButtonEnabled: Boolean = false,
        val questionOneValue: Boolean = false,
        val questionTwoValue: Boolean = false,
        val firstName: String? = null,
        val pkUser: String? = null,
        val changingField: String? = null,
        val titleResource: Int = R.string.empty,
        val newPhoneNumberCode: String? = null
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnQuestionTwoSelected -> onQuestionTwoValueChange(event.value)
            is UIEvent.OnQuestionOneSelected -> onQuestionOneValueChange(event.value)
            is UIEvent.OnNavigateBack -> navigateBack(Screen.ProfilePersonalInfoScreen.route, false)
            is UIEvent.OnContinueButtonClicked -> onContinueButtonClicked()
        }
    }

    sealed class UIEvent {
        data class OnQuestionTwoSelected(val value: Boolean) : UIEvent()
        data class OnQuestionOneSelected(val value: Boolean) : UIEvent()
        object OnContinueButtonClicked : UIEvent()
        object OnNavigateBack : UIEvent()
    }

    companion object {
        const val SEND_PHONE_METHOD = "PHONE"
        const val SEND_EMAIL_METHOD = "EMAIL"
    }
}
