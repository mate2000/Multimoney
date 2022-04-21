package com.multimoney.multimoney.presentation.ui.personal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpPersonalDataViewModel @Inject constructor() : BaseViewModel() {
    var nationalityValue by mutableStateOf("")
    var crPersonalDocument by mutableStateOf("")
    var personalIDError by mutableStateOf(Pair(false, 0))
    var nameError by mutableStateOf(Pair(false, 0))
    var lastNameError by mutableStateOf(Pair(false, 0))
    var personalDocumentValue by mutableStateOf("")
    var nameValue by mutableStateOf("")
    var lastNameValue by mutableStateOf("")
}