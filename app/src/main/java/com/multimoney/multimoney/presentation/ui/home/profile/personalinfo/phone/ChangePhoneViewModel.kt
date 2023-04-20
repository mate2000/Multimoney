package com.multimoney.multimoney.presentation.ui.home.profile.personalinfo.phone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.FieldToChange
import com.multimoney.domain.interaction.security.QueryGetCountryPhoneCodesUseCase
import com.multimoney.domain.model.util.onFailure
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
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.util.isPhoneNumberValid
import com.multimoney.multimoney.presentation.util.transformation.PhoneNumberTransformation
import com.togitech.ccp.data.CountryData
import com.togitech.ccp.data.utils.getLibCountries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChangePhoneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val queryGetCountryPhoneCodesUseCase: QueryGetCountryPhoneCodesUseCase

) : BaseViewModel(true) {
    var phoneNumberTransformation: PhoneNumberTransformation? = null

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
            firstName = savedStateHandle[FIRST_NAME],
            idClient = savedStateHandle[ID_CLIENT]
        )
        phoneNumberTransformation =
            PhoneNumberTransformation(uiState.countryCode?.uppercase().toString())
        uiState = uiState.copy(
            phoneNumberTemplateMinimalLength = phoneNumberTransformation?.mobileTextExample?.text?.replace(
                "\\s".toRegex(),
                ""
            )?.length
        )
    }

    private fun onSetupDefaultCountry(idBrand: Int) {
        uiState = uiState.copy(idBrand = idBrand)
        uiState = when (uiState.idBrand) {
            Brand.Guatemala.id -> uiState.copy(currentBrand = Brand.Guatemala)
            Brand.ElSalvador.id -> uiState.copy(currentBrand = Brand.ElSalvador)
            Brand.Mexico.id -> uiState.copy(currentBrand = Brand.Mexico)
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

    private fun callQueryGetCountryPhoneCodes(idBrand: Int) =
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
                    onSetupDefaultCountry(idBrand)
                }
            }
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
            uiState.phoneCode.plus(uiState.newPhoneNumber) == uiState.phoneNumber?.replace(
                " ",
                ""
            ) ->
                uiState = uiState.copy(
                    isButtonEnabled = false
                )
            else -> uiState = uiState.copy(
                isButtonEnabled = true, phoneNumberError = Pair(false, R.string.error_empty)
            )
        }
    )

    private fun onUserPhoneValueChanged(newPhoneNumber: String, countryCode: String) {
        uiState = uiState.copy(
            newPhoneNumber = newPhoneNumber, phoneNumberError = Pair(false, R.string.error_empty)
        )
        isFormValid(countryCode)
    }

    private fun initCountryCode(): String {
        return when (savedStateHandle.get<Int>(ID_BRAND)) {
            Brand.Guatemala.id -> Brand.Guatemala.countryCode
            Brand.CostaRica.id -> Brand.CostaRica.countryCode
            Brand.ElSalvador.id -> Brand.ElSalvador.countryCode
            Brand.Mexico.id -> Brand.Mexico.countryCode
            else -> Brand.CostaRica.countryCode
        }
    }

    private fun onStart(phoneCode: String) {
        uiState = uiState.copy(phoneCode = phoneCode)
        callQueryGetCountryPhoneCodes(uiState.idBrand ?: Brand.CostaRica.id)
    }

    private fun onCountryCodeValueChanged(phoneCode: String, countryCode: String) {
        uiState = uiState.copy(
            phoneCode = phoneCode,
            newPhoneNumber = "",
            phoneNumberError = Pair(false, R.string.error_empty),
            countryCode = countryCode
        )
        phoneNumberTransformation =
            PhoneNumberTransformation(uiState.countryCode?.uppercase().toString())
        uiState = uiState.copy(
            phoneNumberTemplateMinimalLength = phoneNumberTransformation?.mobileTextExample?.text?.replace(
                "\\s".toRegex(),
                ""
            )?.length
        )
        isFormValid(countryCode)
    }

    private fun isPhoneValid(countryCode: String?) {
        uiState.phoneNumberTemplateMinimalLength?.let { templateLength ->
            uiState.newPhoneNumber?.let { newPhoneNumber ->
                if (newPhoneNumber.length < templateLength) {
                    uiState = uiState.copy(
                        phoneNumberError = Pair(
                            true,
                            R.string.profile_change_phone_check_format_template
                        )
                    )
                } else {
                    if (isPhoneNumberValid(
                            phone = uiState.newPhoneNumber.toString(),
                            fullPhoneNumber = "${uiState.phoneCode}${uiState.newPhoneNumber}",
                            countryCode = countryCode ?: "",
                            phoneNumberType = PhoneNumberUtil.PhoneNumberType.MOBILE
                        ).not()
                    ) uiState = uiState.copy(
                        phoneNumberError = Pair(
                            true,
                            R.string.sign_up_phone_not_valid
                        )
                    )
                    else if (uiState.phoneCode.plus(uiState.newPhoneNumber) == uiState.phoneNumber?.replace(
                            " ",
                            ""
                        )
                    ) {
                        uiState = uiState.copy(
                            phoneNumberError = Pair(
                                true,
                                R.string.profile_phone_not_equal_than_previous_error
                            ), isButtonEnabled = false
                        )
                    } else clearPhoneError()
                }
            }
        }
    }

    private fun clearPhoneError() {
        uiState = uiState.copy(phoneNumberError = Pair(false, R.string.error_empty))
    }

    private fun onContinueButtonClicked() {
        navigateTo("${Screen.ProfileVerifyIdentityPhoneScreen.baseRoute}/${uiState.idClient}/${FieldToChange.PHONE.value}/${uiState.idBrand}/${uiState.pkUser}/${uiState.phoneNumber}/${uiState.newPhoneNumber}/${uiState.email}/${uiState.identification}/${uiState.userName}/${uiState.firstName}/${uiState.phoneCode}")
    }


    data class UIState(
        // Fields
        val userName: String? = null,
        val email: String? = null,
        val identification: String? = null,
        val phoneNumber: String? = null,
        val newPhoneNumber: String? = null,
        val idBrand: Int? = null,
        val idClient: Int? = null,
        val firstName: String? = null,
        val pkUser: String? = null,
        val phoneNumberTemplateMinimalLength: Int? = 0,
        val phoneCode: String = "",
        val countryCode: String? = null,
        val phoneNumberError: Pair<Boolean, Int> = Pair(false, R.string.sign_up_phone_not_valid),
        val isButtonEnabled: Boolean = false,
        val currentBrand: Brand = Brand.CostaRica,
        val selectedCountry: CountryData? = null,
        val countriesList: MutableList<CountryData>? = null,
        val isAlertResultVisible: Boolean = false,
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnUserPhoneValueChanged -> onUserPhoneValueChanged(
                event.phoneNumber, event.countryCode
            )
            is UIEvent.OnStart -> onStart(event.phoneCode)
            is UIEvent.OnValidatePhone -> isPhoneValid(event.countryCode)
            is UIEvent.OnCountryCodeValueChanged -> onCountryCodeValueChanged(
                event.phoneCode, event.countryCode
            )
            is UIEvent.OnContinueButtonClicked -> onContinueButtonClicked()
            is UIEvent.OnNavigateBack -> navigateBack(Screen.ProfilePersonalInfoScreen.route, false)
            is UIEvent.OnSetupDefaultCountry -> onSetupDefaultCountry(
                uiState.idBrand ?: Brand.CostaRica.id
            )
            is UIEvent.OnQueryError -> uiState = uiState.copy(isAlertResultVisible = true)

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
        object OnSetupDefaultCountry : UIEvent()
        object OnQueryError : UIEvent()

    }
}
