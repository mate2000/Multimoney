package com.multimoney.multimoney.presentation.ui.personal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpPersonalDataViewModel @Inject constructor() : BaseViewModel() {
    var nationalityValue by mutableStateOf("")
    var crPersonalDocument by mutableStateOf("")
    var personalIDError by mutableStateOf(Pair(false, R.string.sign_up_dui_required))
    var nameError by mutableStateOf(Pair(false, 0))
    var lastNameError by mutableStateOf(Pair(false, 0))
    var personalDocumentValue by mutableStateOf("")
    var nameValue by mutableStateOf("")
    var lastNameValue by mutableStateOf("")

    fun validDui(): Boolean {
        personalIDError = if (personalDocumentValue.length == 9) {
            val duiSplit = personalDocumentValue.split("").filter { it != "" }
            var verificationNumber = 0
            for (i in duiSplit.indices) {
                if (i != duiSplit.size - 1) {
                    verificationNumber += duiSplit[i].toInt() * (duiSplit.size - i)
                }
            }
            val verificationValue = 10 - verificationNumber.mod(10)
            Pair(
                verificationValue != 10 && verificationValue != duiSplit[duiSplit.lastIndex].toInt(),
                R.string.sign_up_dui_not_valid
            )
        } else {
            Pair(false, R.string.error_empty)
        }
        return validateFields()
    }

    fun validId(sizeRequired: Int, errorMessage: Int): Boolean {
        personalIDError = if (personalDocumentValue.length >= sizeRequired)
            Pair(false, R.string.error_empty) else
            Pair(true, errorMessage)
        return validateFields()
    }

    fun validateFields(): Boolean {
        return when (nationalityValue) {
            EL_SALVADOR -> {
                personalDocumentValue.isNotBlank() && (personalDocumentValue.length == 9) && !personalIDError.first && nameValue.isNotBlank() && lastNameValue.isNotBlank()
            }
            GUATEMALA -> {
                personalDocumentValue.isNotBlank() && (personalDocumentValue.length == 13) && !personalIDError.first && nameValue.isNotBlank() && lastNameValue.isNotBlank()
            }
            COSTA_RICA -> {
                personalDocumentValue.isNotBlank() && (personalDocumentValue.length == 9 || personalDocumentValue.length == 12) && !personalIDError.first && crPersonalDocument.isNotBlank()
            }
            else -> false
        }
    }

    companion object {
        const val EL_SALVADOR = "El Salvador"
        const val GUATEMALA = "Guatemala"
        const val COSTA_RICA = "Costa Rica"
    }
}