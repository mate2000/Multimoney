package com.multimoney.multimoney.presentation.ui.login.signup.personaldata

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.CrDocuments
import com.multimoney.multimoney.presentation.util.Nationalities
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpPersonalDataViewModel @Inject constructor() : BaseViewModel() {
    var nationalityValue by mutableStateOf("")
    var crPersonalDocument by mutableStateOf("")
    var personalIdError by mutableStateOf(
        Pair(
            false,
            R.string.sign_up_personal_data_id_sv_required
        )
    )
    var nameError by mutableStateOf(Pair(false, 0))
    var lastNameError by mutableStateOf(Pair(false, 0))
    var personalDocumentValue by mutableStateOf("")
    var nameValue by mutableStateOf("")
    var lastNameValue by mutableStateOf("")

    fun validateFields(): Boolean {
        return when (nationalityValue) {
            Nationalities.ElSalvador.country -> personalDocumentValue.isNotBlank() && (personalDocumentValue.length == Nationalities.ElSalvador.documentSize) && !personalIdError.first && nameValue.isNotBlank() && lastNameValue.isNotBlank()
            Nationalities.Guatemala.country -> personalDocumentValue.isNotBlank() && (personalDocumentValue.length == Nationalities.Guatemala.documentSize) && !personalIdError.first && nameValue.isNotBlank() && lastNameValue.isNotBlank()
            Nationalities.CostaRicaId.country -> personalDocumentValue.isNotBlank() && (personalDocumentValue.length == Nationalities.CostaRicaId.documentSize || personalDocumentValue.length == Nationalities.CostaRicaDimex.documentSize) && !personalIdError.first && crPersonalDocument.isNotBlank()
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