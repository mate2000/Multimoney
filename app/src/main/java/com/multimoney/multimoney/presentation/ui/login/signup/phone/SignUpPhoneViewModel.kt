package com.multimoney.multimoney.presentation.ui.login.signup.phone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.MOBILE
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.UIEvent.OnClearPhoneError
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.UIEvent.OnCountryCodeValueChanged
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.UIEvent.OnUserPhoneValueChanged
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.UIEvent.OnValidatePhone
import com.multimoney.multimoney.presentation.util.isPhoneNumberValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpPhoneViewModel @Inject constructor() : BaseViewModel() {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onStart(phoneCode: String, phoneNumber: String, signUpStartData: () -> Unit) {
        uiState = uiState.copy(phoneCode = phoneCode, phoneNumber = phoneNumber)
        signUpStartData()
    }

    private fun isFormValid(countryCode: String) = emitBaseEvent(
        OnFormValidateCompleted(
            when {
                uiState.phoneCode.isBlank() || uiState.phoneNumber.isBlank() -> false
                isPhoneNumberValid(
                    phone = uiState.phoneNumber,
                    fullPhoneNumber = "${uiState.phoneCode}${uiState.phoneNumber}",
                    countryCode = countryCode,
                    phoneNumberType = MOBILE
                ) -> false
                else -> true
            }
        )
    )

    private fun onUserPhoneValueChanged(value: String, updateUserInfoPhone: () -> Unit) {
        uiState =
            uiState.copy(phoneNumber = value, phoneNumberError = Pair(false, R.string.error_empty))
        isFormValid(uiState.phoneCode)
        updateUserInfoPhone.invoke()
    }

    private fun onCountryCodeValueChanged(value: String, updateUserCountryCode: () -> Unit) {
        uiState = uiState.copy(
            phoneCode = value,
            phoneNumber = "",
            phoneNumberError = Pair(false, R.string.error_empty)
        )
        isFormValid(uiState.phoneCode)
        updateUserCountryCode.invoke()
    }

    private fun isPhoneValid(countryCode: String) {
        if (isPhoneNumberValid(
                phone = uiState.phoneNumber,
                fullPhoneNumber = "${uiState.phoneCode}${uiState.phoneNumber}",
                countryCode = countryCode,
                phoneNumberType = MOBILE
            ).not()
        ) uiState = uiState.copy(phoneNumberError = Pair(true, R.string.sign_up_phone_not_valid))
    }

    private fun clearPhoneError() {
        uiState = uiState.copy(phoneNumberError = Pair(false, R.string.error_empty))
    }

    private fun onNextActionClick(
        onUserDataValueChange: () -> Unit,
        onCallMutationUpdateUserRegisterUseCase: () -> Unit
    ) {
        onUserDataValueChange()
        onCallMutationUpdateUserRegisterUseCase()
    }

    data class UIState(
        // Fields
        val phoneCode: String = "",
        val phoneNumber: String = "",
        val phoneNumberError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_phone_not_valid),
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnValidateForm -> isFormValid(event.countryCode)
            is OnValidatePhone -> isPhoneValid(event.countryCode)
            is OnUserPhoneValueChanged -> onUserPhoneValueChanged(
                event.phoneNumber,
                event.updateUserInfoPhone
            )
            is OnCountryCodeValueChanged -> onCountryCodeValueChanged(
                event.countryCode,
                event.updateUserCountryCode
            )
            is OnNextActionClick -> onNextActionClick(
                event.onUserDataValueChange,
                event.onCallMutationUpdateUserRegisterUseCase
            )
            is OnClearPhoneError -> clearPhoneError()
            is OnStart -> onStart(event.phoneCode, event.phoneNumber, event.signUpStartData)
        }
    }

    sealed class UIEvent {
        data class OnValidateForm(val countryCode: String) : UIEvent()
        data class OnValidatePhone(val countryCode: String) : UIEvent()
        data class OnUserPhoneValueChanged(
            val phoneNumber: String,
            val updateUserInfoPhone: () -> Unit
        ) : UIEvent()

        data class OnCountryCodeValueChanged(
            val countryCode: String,
            val updateUserCountryCode: () -> Unit
        ) : UIEvent()

        data class OnNextActionClick(
            val onUserDataValueChange: () -> Unit,
            val onCallMutationUpdateUserRegisterUseCase: () -> Unit
        ) : UIEvent()

        data class OnStart(
            val phoneCode: String,
            val phoneNumber: String,
            val signUpStartData: () -> Unit
        ) : UIEvent()

        object OnClearPhoneError : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}