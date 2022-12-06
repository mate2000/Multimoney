package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.phone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
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
import com.multimoney.multimoney.presentation.util.isPhoneNumberValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePhoneViewModel @Inject constructor(
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
            countryCode = initCountryCode(),
            pkUser = savedStateHandle[PK_USER],
            firstName = savedStateHandle[FIRST_NAME]
        )
    }

    private fun isFormValid(countryCode: String) = emitBaseEvent(
        when {
            uiState.phoneCode.isBlank() || uiState.newPhoneNumber?.isBlank() == true -> uiState =
                uiState.copy(isButtonEnabled = false)
            isPhoneNumberValid(
                phone = uiState.newPhoneNumber.toString(),
                fullPhoneNumber = "${uiState.phoneCode}${uiState.newPhoneNumber}",
                countryCode = countryCode,
                phoneNumberType = PhoneNumberUtil.PhoneNumberType.MOBILE
            ).not() -> uiState = uiState.copy(isButtonEnabled = false)
            else -> uiState = uiState.copy(
                isButtonEnabled = true, phoneNumberError = Pair(false, R.string.error_empty)
            )
        }
    )

    private fun onUserPhoneValueChanged(newPhoneNumber: String, countryCode: String) {
        uiState =
            uiState.copy(
                newPhoneNumber = newPhoneNumber,
                phoneNumberError = Pair(false, R.string.error_empty)
            )
        isFormValid(countryCode)
    }

    private fun initCountryCode(): String {
        return when (savedStateHandle.get<Int>(ID_BRAND)) {
            Brand.Guatemala.id -> Brand.Guatemala.countryCode
            Brand.CostaRica.id -> Brand.CostaRica.countryCode
            Brand.ElSalvador.id -> Brand.ElSalvador.countryCode
            else -> ""
        }
    }

    private fun onStart(phoneCode: String) {
        uiState = uiState.copy(phoneCode = phoneCode)
    }

    private fun onCountryCodeValueChanged(phoneCode: String, countryCode: String) {
        uiState = uiState.copy(
            phoneCode = phoneCode,
            newPhoneNumber = "",
            phoneNumberError = Pair(false, R.string.error_empty),
            countryCode = countryCode
        )
        isFormValid(countryCode)
    }

    private fun isPhoneValid(countryCode: String?) {
        if (isPhoneNumberValid(
                phone = uiState.newPhoneNumber.toString(),
                fullPhoneNumber = "${uiState.phoneCode}${uiState.newPhoneNumber}",
                countryCode = countryCode ?: "",
                phoneNumberType = PhoneNumberUtil.PhoneNumberType.MOBILE
            ).not()
        ) uiState = uiState.copy(phoneNumberError = Pair(true, R.string.sign_up_phone_not_valid))
        else clearPhoneError()
    }

    private fun clearPhoneError() {
        uiState = uiState.copy(phoneNumberError = Pair(false, R.string.error_empty))
    }

    private fun onContinueButtonClicked() {
        navigateTo("${Screen.ProfileVerifyIdentityPhoneScreen.baseRoute}/${FieldToChange.PHONE.value}/${uiState.idBrand}/${uiState.pkUser}/${uiState.phoneNumber}/${uiState.newPhoneNumber}/${uiState.email}/${uiState.identification}/${uiState.userName}/${uiState.firstName}")
    }

    data class UIState(
        // Fields
        val userName: String? = null,
        val email: String? = null,
        val identification: String? = null,
        val phoneNumber: String? = null,
        val newPhoneNumber: String? = null,
        val idBrand: Int? = null,
        val firstName: String? = null,
        val pkUser: String? = null,
        val phoneCode: String = "",
        val countryCode: String? = null,
        val phoneNumberError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_phone_not_valid),
        val isButtonEnabled: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnUserPhoneValueChanged -> onUserPhoneValueChanged(
                event.phoneNumber,
                event.countryCode
            )
            is UIEvent.OnStart -> onStart(event.phoneCode)
            is UIEvent.OnValidatePhone -> isPhoneValid(event.countryCode)
            is UIEvent.OnCountryCodeValueChanged -> onCountryCodeValueChanged(
                event.phoneCode,
                event.countryCode
            )
            is UIEvent.OnContinueButtonClicked -> onContinueButtonClicked()
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
        }
    }

    sealed class UIEvent {
        data class OnUserPhoneValueChanged(val phoneNumber: String, val countryCode: String) :
            UIEvent()
        data class OnStart(val phoneCode: String) : UIEvent()
        data class OnValidatePhone(val countryCode: String?) : UIEvent()
        data class OnCountryCodeValueChanged(
            val phoneCode: String,
            val countryCode: String,
        ) : UIEvent()
        object OnContinueButtonClicked : UIEvent()
        object OnNavigateBack : UIEvent()
    }
}

