package com.multimoney.multimoney.presentation.ui.login.signup.personaldata

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryDataInformationClientUseCase
import com.multimoney.domain.model.security.ClientInfoCr
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.util.CrDocuments
import com.multimoney.multimoney.presentation.util.Nationalities
import com.multimoney.multimoney.presentation.util.validId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpPersonalDataViewModel @Inject constructor(
    val queryDataInformationClientUseCase: QueryDataInformationClientUseCase
) : BaseViewModel() {

    //Fields
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
    var closeKeyboard by mutableStateOf(false)

    // Interactions
    var onSuccessDataInformationClient by mutableStateOf<ClientInfoCr?>(null)
    var isFirstLaunch = false

    fun validateFields(): Boolean {
        return when (nationalityValue) {
            Nationalities.ElSalvador.country -> personalDocumentValue.isNotBlank() && (personalDocumentValue.length == Nationalities.ElSalvador.documentSize) && !personalIdError.first && nameValue.isNotBlank() && lastNameValue.isNotBlank()
            Nationalities.Guatemala.country -> personalDocumentValue.isNotBlank() && (personalDocumentValue.length == Nationalities.Guatemala.documentSize) && !personalIdError.first && nameValue.isNotBlank() && lastNameValue.isNotBlank()
            Nationalities.CostaRicaId.country -> personalDocumentValue.isNotBlank() && (personalDocumentValue.length == Nationalities.CostaRicaId.documentSize || personalDocumentValue.length == Nationalities.CostaRicaDimex.documentSize) && !personalIdError.first && crPersonalDocument.isNotBlank()
            else -> false
        }
    }

    fun getNationality(nationality: String) = when (nationality) {
        Nationalities.ElSalvador.country -> Nationalities.ElSalvador.name
        Nationalities.Guatemala.country -> Nationalities.Guatemala.name
        else -> Nationalities.CostaRicaId.name
    }

    fun crFilterDocument(id: String) {
        if (crPersonalDocument == CrDocuments.IdDocument.document && id.length <= Nationalities.CostaRicaId.documentSize) {
            personalDocumentValue = id.filter { it.isDigit() }
        } else if (crPersonalDocument == CrDocuments.Dimex.document && id.length <= Nationalities.CostaRicaDimex.documentSize) {
            personalDocumentValue = id.filter { it.isDigit() }
        }
    }

    fun validateCrDocument(
        user: String,
    ) {
        val status = validId(
            if (crPersonalDocument == CrDocuments.IdDocument.document) Nationalities.CostaRicaId.documentSize else Nationalities.CostaRicaDimex.documentSize,
            R.string.sign_up_personal_data_id_not_valid,
            personalDocumentValue.length
        )
        personalIdError = status
        if (status.first.not()) {
            closeKeyboard = true
            callQueryDataInformationClient(personalDocumentValue, Brand.Revamp.id, user)
        }
    }

    private fun callQueryDataInformationClient(
        identification: String,
        idBrant: Int,
        user: String,
    ) {
        viewModelScope.launch {
            queryDataInformationClientUseCase.invoke(
                identification,
                idBrant,
                user
            ).collectLatest { result ->
                result.onSuccess {
                    onSuccessDataInformationClient = it
                    isLoading = false
                }
                result.onFailure {
                    isLoading = false
                    onSuccessDataInformationClient = null
                    personalIdError = Pair(true, R.string.sign_up_personal_data_id_not_valid)
                }
                result.onLoading {
                    isLoading = true
                }
            }
        }
    }

    companion object {
        const val DUI_VERIFICATION_MODULE = 10
    }
}