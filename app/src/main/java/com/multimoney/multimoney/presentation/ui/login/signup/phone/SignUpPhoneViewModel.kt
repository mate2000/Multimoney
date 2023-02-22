package com.multimoney.multimoney.presentation.ui.login.signup.phone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.MOBILE
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SignUpStep
import com.multimoney.domain.interaction.security.QueryGetCountryPhoneCodesUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.phone.SignUpPhoneViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.util.isPhoneNumberValid
import com.togitech.ccp.data.CountryData
import com.togitech.ccp.data.utils.getLibCountries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SignUpPhoneViewModel @Inject constructor(
    private val queryGetCountryPhoneCodesUseCase: QueryGetCountryPhoneCodesUseCase
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun onSetupDefaultCountry(idBrand: Int) {
        uiState = uiState.copy(idBrand = idBrand)
        uiState = when (uiState.idBrand) {
            Brand.Guatemala.id -> uiState.copy(currentBrand = Brand.Guatemala)
            Brand.ElSalvador.id -> uiState.copy(currentBrand = Brand.ElSalvador)
            else -> uiState.copy(currentBrand = Brand.CostaRica)
        }
        val defaultCountry =
            getLibCountries.find { it.countryCode == uiState.currentBrand.countryCode }
        if (defaultCountry != null) {
            uiState =
                uiState.copy(countriesList = mutableListOf(defaultCountry))
        }
        uiState = uiState.copy(selectedCountry = uiState.countriesList?.first())
    }

    private fun onStart(
        phoneCode: String,
        phoneNumber: String,
        signUpStartData: () -> Unit,
        onFailure: () -> Unit
    ) {
        uiState = uiState.copy(phoneCode = phoneCode, phoneNumber = phoneNumber)
        signUpStartData.invoke()
        callQueryGetCountryPhoneCodes(uiState.idBrand, onFailure)
        isFormValid(phoneCode)
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

    private fun callQueryGetCountryPhoneCodes(idBrand: Int, onFailure: () -> Unit) =
        executeUseCase {
            queryGetCountryPhoneCodesUseCase.invoke(idBrand = idBrand).collectLatest { result ->
                result.onSuccess { response ->
                    uiState = uiState.copy(countriesList = mutableListOf())
                    val list = response.countryPhoneCodes.flatMap { fromApi ->
                        getLibCountries.filter { fromApi.isoCode.lowercase(Locale.getDefault()) == it.countryCode }
                    }
                    uiState = uiState.copy(
                        countriesList = list.toMutableList(),
                        selectedCountry = list.toMutableList().first()
                    )
                }.onFailure {
                    onFailure.invoke()
                }
            }
        }

    private fun onSetUpIdBrand(idBrand: Int) {
        uiState = uiState.copy(idBrand = idBrand)
    }

    private fun onQueryError() {
        uiState = uiState.copy(isAlertResultVisible = true)
    }

    data class UIState(
        // Fields
        val phoneCode: String = "",
        val phoneNumber: String = "",
        val phoneNumberError: Pair<Boolean, Int> = Pair(
            false,
            R.string.sign_up_phone_not_valid
        ),
        val countriesList: MutableList<CountryData>? = null,
        val countryCode: String? = null,
        val idBrand: Int = 0,
        val selectedCountry: CountryData? = null,
        val isAlertResultVisible: Boolean = false,
        val currentBrand: Brand = Brand.CostaRica
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnValidatePhone -> isPhoneValid(event.countryCode)
            is UIEvent.OnUserPhoneValueChanged -> onUserPhoneValueChanged(
                event.phoneNumber,
                event.countryCode,
                event.updateUserInfoPhone
            )
            is UIEvent.OnCountryCodeValueChanged -> onCountryCodeValueChanged(
                event.phoneCode,
                event.countryCode,
                event.updateUserCountryCode
            )
            is UIEvent.OnNextActionClick -> onNextActionClick(
                event.onUserDataValueChange,
                event.onCallMutationUpdateUserRegisterUseCase
            )
            is UIEvent.OnClearPhoneError -> clearPhoneError()
            is UIEvent.OnStart -> onStart(
                event.phoneCode,
                event.phoneNumber,
                event.signUpStartData,
                event.onFailure
            )
            is UIEvent.OnSetUpIdBrand -> onSetUpIdBrand(event.idBrand)
            is UIEvent.OnQueryError -> onQueryError()
            is UIEvent.OnSetupDefaultCountry -> onSetupDefaultCountry(event.idBrand)
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
            val onFailure: () -> Unit
        ) : UIEvent()

        object OnClearPhoneError : UIEvent()
        object OnQueryError : UIEvent()
        data class OnSetUpIdBrand(val idBrand: Int) : UIEvent()
        data class OnSetupDefaultCountry(val idBrand: Int) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}
