package com.multimoney.multimoney.presentation.ui.login.signup.phone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpPhoneViewModel @Inject constructor() : BaseViewModel() {

    // Fields
    var whatsapp by mutableStateOf(true)
    var call by mutableStateOf(true)

    // TODO: Remove email fields
    var userEmail by mutableStateOf("")
    var userEmailError by mutableStateOf(Pair(false, R.string.sign_up_email_required))

    fun isFormValid(): Boolean {
        return false
    }
}