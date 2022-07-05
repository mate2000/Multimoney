package com.multimoney.multimoney.presentation.ui.login.signup.personaldata

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryDataInformationClientUseCase
import com.multimoney.domain.model.security.ClientInfoCr
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.SignUpViewModel.UIEvent
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnFirstNameChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnNationalityChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.util.CrDocuments
import com.multimoney.multimoney.presentation.util.DialogParameters
import com.multimoney.multimoney.presentation.util.Nationalities
import com.multimoney.multimoney.presentation.util.validId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpPersonalDataViewModel @Inject constructor(
    private val queryDataInformationClientUseCase: QueryDataInformationClientUseCase
) : BaseViewModel() {
    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    var crPersonalDocument by mutableStateOf("")

    var personalIdError by mutableStateOf(
        Pair(
            false,
            R.string.sign_up_personal_data_id_sv_required
        )
    )
    var nameError by mutableStateOf(Pair(false, 0))
    var lastNameError by mutableStateOf(Pair(false, 0))
    var closeKeyboard by mutableStateOf(false)

    // Interactions
    var onSuccessDataInformationClient by mutableStateOf<ClientInfoCr?>(null)

    fun validateFields(): Boolean {
        return when (uiState.nationalityValue) {
            Nationalities.ElSalvador.country -> uiState.personalDocumentValue.isNotBlank() && (uiState.personalDocumentValue.length == Nationalities.ElSalvador.documentSize) && !personalIdError.first && uiState.firstNameValue.isNotBlank() && uiState.firstLastNameValue.isNotBlank()
            Nationalities.Guatemala.country -> uiState.personalDocumentValue.isNotBlank() && (uiState.personalDocumentValue.length == Nationalities.Guatemala.documentSize) && !personalIdError.first && uiState.firstNameValue.isNotBlank() && uiState.firstLastNameValue.isNotBlank()
            Nationalities.CostaRicaId.country -> uiState.personalDocumentValue.isNotBlank() &&
                    (uiState.personalDocumentValue.length == Nationalities.CostaRicaId.documentSize || uiState.personalDocumentValue.length == Nationalities.CostaRicaDimex.documentSize) &&
                    !personalIdError.first && crPersonalDocument.isNotBlank() && onSuccessDataInformationClient?.fullName != null
            else -> false
        }
    }

    private fun getNationality(country: String) = when (country) {
        Nationalities.ElSalvador.country -> Nationalities.ElSalvador.name
        Nationalities.Guatemala.country -> Nationalities.Guatemala.name
        else -> Nationalities.CostaRicaId.name
    }

    private fun getCountry(nationality: String) = when (nationality) {
        Nationalities.ElSalvador.name -> Nationalities.ElSalvador.country
        Nationalities.Guatemala.name -> Nationalities.Guatemala.country
        Nationalities.CostaRicaId.name -> Nationalities.CostaRicaId.country
        else -> ""
    }

    fun crFilterDocument(id: String) {
        if (crPersonalDocument == CrDocuments.IdDocument.document && id.length <= Nationalities.CostaRicaId.documentSize) {
            uiState = uiState.copy(crPersonalDocumentValue = id.filter { it.isDigit() })
        } else if (crPersonalDocument == CrDocuments.Dimex.document && id.length <= Nationalities.CostaRicaDimex.documentSize) {
            uiState = uiState.copy(crPersonalDocumentValue = id.filter { it.isDigit() })
        }
    }

    fun validateCrDocument(
        user: String,
    ) {
        val status = validId(
            if (crPersonalDocument == CrDocuments.IdDocument.document) Nationalities.CostaRicaId.documentSize else Nationalities.CostaRicaDimex.documentSize,
            R.string.sign_up_personal_data_id_not_valid,
            uiState.personalDocumentValue.length
        )
        personalIdError = status
        if (status.first.not()) {
            closeKeyboard = true
            callQueryDataInformationClient(uiState.personalDocumentValue, Brand.Revamp.id, user)
        } else {
            if (onSuccessDataInformationClient?.fullName.isNullOrBlank().not()) {
                onSuccessDataInformationClient = null
            }
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
                    validateFields()
                }
                result.onFailure {
                    isLoading = false
                    onSuccessDataInformationClient = null
                    personalIdError = Pair(true, R.string.sign_up_personal_data_id_not_valid)
                    validateFields()
                }
                result.onLoading {
                    isLoading = true
                }
            }
        }
    }

    private fun onNationalityChange(
        nationality: String,
        updateNationality: (nationality: String) -> Unit
    ) {
        uiState = uiState.copy(nationalityValue = nationality, personalDocumentValue = "")
        updateNationality.invoke(getNationality(nationality))
    }

    private fun onFirstNameChange(firstName: String) {
        uiState = uiState.copy(firstLastNameValue = firstName.filter { it.isDigit() })
    }

    private fun onStart(
        nationality: String,
        crPersonalDocument: String,
        identification: String,
        firstName: String,
        secondName: String,
        firstLastName: String,
        secondLastName: String,
        fullName: String
    ) {
        uiState = uiState.copy(
            nationalityValue = getCountry(nationality),
            crPersonalDocumentValue = crPersonalDocument,
            personalDocumentValue = identification,
            firstNameValue = firstName,
            secondNameValue = secondName,
            firstLastNameValue = firstLastName,
            secondLastNameValue = secondLastName,
            fullNameValue = fullName
        )
    }

    private fun cleanUI() {
        uiState = uiState.copy(
            nationalityValue = "",
            crPersonalDocument = "",
            personalIdError = Pair(false, R.string.sign_up_personal_data_id_sv_required),
            nameError = Pair(false, R.string.sign_up_personal_data_id_sv_required),
            lastNameError = Pair(false, R.string.sign_up_personal_data_id_sv_required),
            personalDocumentValue = "",
            nameValue = "",
            lastNameValue = "",
            closeKeyboard = false
        )
    }

    data class UIState(
        val nationalityValue: String = "",
        val crPersonalDocumentValue: String = "",
        val personalDocumentValue: String = "",
        val firstNameValue: String = "",
        val secondNameValue: String = "",
        val firstLastNameValue: String = "",
        val secondLastNameValue: String = "",
        val fullNameValue: String = "",
        val crPersonalDocument: String = "",
        val personalIdError: Pair<Boolean, Int> = Pair(
            false,
            R.string.sign_up_personal_data_id_sv_required
        ),

        val nameError: Pair<Boolean, Int> = Pair(false, 0),
        val lastNameError: Pair<Boolean, Int> = Pair(false, 0),
        val nameValue: String = "",
        val lastNameValue: String = "",
        val closeKeyboard: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnStart -> onStart(
                event.nationality,
                event.crPersonalDocumentValue,
                event.identification,
                event.firstName,
                event.secondName,
                event.firstLastName,
                event.secondLastName,
                event.fullName
            )
            is OnNationalityChange -> onNationalityChange(
                event.nationality,
                event.updateNationality
            )
            is OnFirstNameChange -> onFirstNameChange(event.firstName)
        }
    }

    sealed class UIEvent {
        data class OnStart(
            val nationality: String,
            val crPersonalDocumentValue: String,
            val identification: String,
            val firstName: String,
            val secondName: String,
            val firstLastName: String,
            val secondLastName: String,
            val fullName: String
        ) : UIEvent()

        data class OnNationalityChange(
            val nationality: String,
            val updateNationality: (nationality: String) -> Unit
        ) :
            UIEvent()

        data class OnFirstNameChange(val firstName: String) : UIEvent()
        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
        data class OnCloseClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueClick(val focusManager: FocusManager) : UIEvent()
        data class OnContinueEnable(val enable: Boolean) : UIEvent()
        data class OnIsBiometricAvailable(val value: Boolean) : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnOpenDialogValueChange(val openDialog: DialogParameters) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnNextActionValueChange(val nextAction: () -> Unit) : UIEvent()
        data class OnUseDataValueChange(val userData: UserData?) : UIEvent()
        data class OnMoveToStep(val step: Int) : UIEvent()

        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val DUI_VERIFICATION_MODULE = 10
    }
}