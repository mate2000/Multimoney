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
        personalIDError = if (personalDocumentValue.length == Nationalities.ElSalvador.documentSize) {
            val duiSplit = personalDocumentValue.split("").filter { it != "" }
            var verificationNumber = 0
            for (i in duiSplit.indices) {
                if (i != duiSplit.size - 1) {
                    verificationNumber += duiSplit[i].toInt() * (duiSplit.size - i)
                }
            }
            val verificationValue = 10 - verificationNumber.mod(DUI_VERIFICATION_MODULE)
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
            Nationalities.ElSalvador.country -> personalDocumentValue.isNotBlank() && (personalDocumentValue.length == Nationalities.ElSalvador.documentSize) && !personalIDError.first && nameValue.isNotBlank() && lastNameValue.isNotBlank()
            Nationalities.Guatemala.country -> personalDocumentValue.isNotBlank() && (personalDocumentValue.length == Nationalities.Guatemala.documentSize) && !personalIDError.first && nameValue.isNotBlank() && lastNameValue.isNotBlank()
            Nationalities.CostaRicaId.country -> personalDocumentValue.isNotBlank() && (personalDocumentValue.length == Nationalities.CostaRicaId.documentSize || personalDocumentValue.length == Nationalities.CostaRicaDimex.documentSize) && !personalIDError.first && crPersonalDocument.isNotBlank()
            else -> false
        }
    }

    fun crFilterDocument(id: String) {
        if (crPersonalDocument == CrDocuments.IdDocument.document && id.length <= Nationalities.CostaRicaId.documentSize) {
            personalDocumentValue = id.filter { it.isDigit() }
        } else if (crPersonalDocument == CrDocuments.Dimex.document && id.length <= Nationalities.CostaRicaDimex.documentSize) {
            personalDocumentValue = id.filter { it.isDigit() }
        }
    }

    companion object {
        const val DUI_VERIFICATION_MODULE = 10
    }
}