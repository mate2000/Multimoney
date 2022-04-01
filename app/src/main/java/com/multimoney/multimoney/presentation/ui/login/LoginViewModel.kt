package com.multimoney.multimoney.presentation.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {

    var userEmail by mutableStateOf("")
    var userName by mutableStateOf<String?>(null)
    var userPassword by mutableStateOf("")
    var isFingerprintChecked by mutableStateOf(false)

    fun login() {}
}