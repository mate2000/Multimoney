package com.multimoney.multimoney.presentation.ui.login.signup.phone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.MOBILE
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.interaction.security.QueryGetCountryPhoneCodesUseCase
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.UIEvent.*
import com.multimoney.multimoney.presentation.util.isPhoneNumberValid
import com.togitech.ccp.data.CountryData
import com.togitech.ccp.data.utils.getLibCountries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SignUpPhoneViewModel @Inject constructor(
    private val queryGetCountryPhoneCodesUseCase: QueryGetCountryPhoneCodesUseCase,

    ) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onStart(
        phoneCode: String,
        countryCode: String,
        phoneNumber: String,
        signUpStartData: () -> Unit,
        idBrand: Int
    ) {
        uiState = uiState.copy(phoneCode = phoneCode, phoneNumber = phoneNumber, idBrand = idBrand)
        signUpStartData.invoke()
        isFormValid(countryCode)
        callQueryGetCountryPhoneCodes(uiState.idBrand)

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
                ).not() -> false
                else -> true
            }
        )
    )

    private fun onUserPhoneValueChanged(
        phoneNumber: String,
        countryCode: String,
        updateUserInfoPhone: () -> Unit
    ) {
        uiState =
            uiState.copy(
                phoneNumber = phoneNumber,
                phoneNumberError = Pair(false, R.string.error_empty)
            )
        isFormValid(countryCode)
        updateUserInfoPhone.invoke()
    }

    private fun onCountryCodeValueChanged(
        phoneCode: String,
        countryCode: String,
        updateUserCountryCode: () -> Unit
    ) {
        uiState = uiState.copy(
            phoneCode = phoneCode,
            phoneNumber = "",
            phoneNumberError = Pair(false, R.string.error_empty)
        )
        isFormValid(countryCode)
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

    fun getNextStep(isPhoneVerified: Boolean, isOnFidoVerified: Boolean) =
        if (isPhoneVerified.not()) {
            SignUpStep.Four
        } else if (isOnFidoVerified.not()) {
            SignUpStep.Five
        } else {
            SignUpStep.Six
        }

    private fun callQueryGetCountryPhoneCodes(idBrand: Int) = executeUseCase {
        queryGetCountryPhoneCodesUseCase.invoke(idBrand = idBrand).collectLatest { result ->
            result.onSuccess { response ->
                val newCountriesList = mutableListOf<CountryData>()
                uiState = uiState.copy(countriesList = mutableListOf())
                response.countryPhoneCodes.forEach { country ->
                    val newCountry = getLibCountries().find { countryFromLibrary ->
                        countryFromLibrary.countryPhoneCode.replace("+", "").toInt() == country.code
                    }
                    if (newCountry != null) {
                        newCountriesList.add(CountryData(cNames = country.country, cCodes = newCountry.countryCode, countryPhoneCode = newCountry.countryPhoneCode))
                    }
                }
                uiState = uiState.copy(countriesList = newCountriesList)
            }
        }
    }

    private fun onSetUpIdBrand(idBrand: Int) {
        uiState = uiState.copy(idBrand = idBrand)
    }

    private fun initCountryCode(): String {
        return when (uiState.idBrand) {
            Brand.Guatemala.id -> Brand.Guatemala.countryCode
            Brand.CostaRica.id -> Brand.CostaRica.countryCode
            Brand.ElSalvador.id -> Brand.ElSalvador.countryCode
            else -> ""
        }
    }

    data class UIState(
        // Fields
        val phoneCode: String = "",
        val phoneNumber: String = "",
        val phoneNumberError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_phone_not_valid),
        val countriesList: MutableList<CountryData>? = null,
        val countryCode: String? = null,
        val idBrand: Int = 0,
        val selectedCountry: CountryData = CountryData("", "", "")
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnValidatePhone -> isPhoneValid(event.countryCode)
            is OnUserPhoneValueChanged -> onUserPhoneValueChanged(
                event.phoneNumber,
                event.countryCode,
                event.updateUserInfoPhone
            )
            is OnCountryCodeValueChanged -> onCountryCodeValueChanged(
                event.phoneCode,
                event.countryCode,
                event.updateUserCountryCode
            )
            is OnNextActionClick -> onNextActionClick(
                event.onUserDataValueChange,
                event.onCallMutationUpdateUserRegisterUseCase
            )
            is OnClearPhoneError -> clearPhoneError()
            is OnStart -> onStart(
                event.phoneCode,
                event.countryCode,
                event.phoneNumber,
                event.signUpStartData,
                event.idBrand
            )
            is UIEvent.OnSetUpIdBrand -> onSetUpIdBrand(event.idBrand)
        }
    }

    sealed class UIEvent {
        data class OnValidatePhone(val countryCode: String) : UIEvent()
        data class OnUserPhoneValueChanged(
            val phoneNumber: String,
            val countryCode: String,
            val updateUserInfoPhone: () -> Unit
        ) : UIEvent()

        data class OnCountryCodeValueChanged(
            val phoneCode: String,
            val countryCode: String,
            val updateUserCountryCode: () -> Unit
        ) : UIEvent()

        data class OnNextActionClick(
            val onUserDataValueChange: () -> Unit,
            val onCallMutationUpdateUserRegisterUseCase: () -> Unit
        ) : UIEvent()

        data class OnStart(
            val phoneCode: String,
            val countryCode: String,
            val phoneNumber: String,
            val signUpStartData: () -> Unit,
            val idBrand: Int
        ) : UIEvent()

        object OnClearPhoneError : UIEvent()
        data class OnSetUpIdBrand(val idBrand: Int) : UIEvent()

    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}