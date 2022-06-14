package com.multimoney.multimoney.presentation.ui.login.signup.phone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.multimoney.domain.model.security.ContactMeans
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.isPhoneNumberValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpPhoneViewModel @Inject constructor() : BaseViewModel() {

    // Data
    var contactMeans: ContactMeans = ContactMeans()

    // Fields
    var phoneCode by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var phoneNumberError by mutableStateOf(Pair(false, R.string.error_empty))
    var whatsapp by mutableStateOf(true)
    var call by mutableStateOf(true)

    fun isFormValid(countryCode: String) = isPhoneNumberValid(
        phone = phoneNumber,
        fullPhoneNumber = "$phoneCode${phoneNumber}",
        countryCode = countryCode,
        phoneNumberType = PhoneNumberUtil.PhoneNumberType.MOBILE
    )

    fun isPhoneValid(countryCode: String) {
        if (isFormValid(countryCode).not()) {
            phoneNumberError = Pair(true, R.string.sign_up_phone_not_valid)
        }
    }

    fun clearPhoneError() {
        phoneNumberError = Pair(false, R.string.error_empty)
    }
}