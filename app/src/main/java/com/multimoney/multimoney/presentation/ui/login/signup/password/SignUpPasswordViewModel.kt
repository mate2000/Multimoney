package com.multimoney.multimoney.presentation.ui.login.signup.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpPasswordViewModel @Inject constructor() : BaseViewModel() {

    var userPassword by mutableStateOf("")
    var userPasswordError by mutableStateOf(Pair(false, R.string.error_empty))
    var confirmPassword by mutableStateOf("")
    var confirmPasswordError by mutableStateOf(Pair(false, R.string.error_empty))

    var eightCharactersMinimum by mutableStateOf<Boolean?>(null)
    var oneUppercase by mutableStateOf<Boolean?>(null)
    var oneLowercase by mutableStateOf<Boolean?>(null)
    var oneNumber by mutableStateOf<Boolean?>(null)
}