package com.multimoney.multimoney.presentation.ui.login.signup.personaldata

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Nationalities.CostaRicaDimex
import com.multimoney.data.util.catalog.Nationalities.CostaRicaId
import com.multimoney.data.util.catalog.Nationalities.ElSalvador
import com.multimoney.data.util.catalog.Nationalities.Guatemala
import com.multimoney.domain.interaction.security.MutationUserValidationUseCase
import com.multimoney.domain.interaction.security.QueryCatalogDocumentTypeUseCase
import com.multimoney.domain.interaction.security.QueryDataInformationClientUseCase
import com.multimoney.domain.interaction.security.QueryGetCountryUseCase
import com.multimoney.domain.model.security.CatalogType
import com.multimoney.domain.model.security.ClientInfoCr
import com.multimoney.domain.model.security.CountryList
import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.BaseEvent.OnGetCountriesSuccess
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnCallQueryGetCountry
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnFirstLastNameChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnFirstNameChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnIdentificationTypeValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnIdentificationValueChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnNationalityChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnSecondLastNameChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnSecondNameChange
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnStart
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnUserDataValidationSuccess
import com.multimoney.multimoney.presentation.ui.login.signup.personaldata.SignUpPersonalDataViewModel.UIEvent.OnValidateDocument
import com.multimoney.multimoney.presentation.util.CrDocuments
import com.multimoney.multimoney.presentation.util.validDui
import com.multimoney.multimoney.presentation.util.validId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpPersonalDataViewModel @Inject constructor(
    private val queryDataInformationClientUseCase: QueryDataInformationClientUseCase,
    private val queryCatalogDocumentTypeUseCase: QueryCatalogDocumentTypeUseCase,
    private val queryGetCountryUseCase: QueryGetCountryUseCase,
    private val mutationUserValidationUseCase: MutationUserValidationUseCase,
) : BaseViewModel(false) {
    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    var closeKeyboard by mutableStateOf(false)

    // Interactions
    private var onSuccessCatalogDocumentType: CatalogType? = null
    private var onSuccessCountry: CountryList? = null

    // Stateless
    var documentLength = 0

    //Event
    val onUserDataValidationEvent = MutableSharedFlow<MultimoneyResult<UserData?>>()

    private fun isFormValid() = emitBaseEvent(
        OnFormValidateCompleted(
            when (uiState.nationalityValue) {
                ElSalvador.country -> uiState.personalDocumentValue.isNotBlank() && (uiState.personalDocumentValue.length == ElSalvador.documentSize) && !uiState.personalIdError.first && uiState.firstNameValue.isNotBlank() && uiState.firstLastNameValue.isNotBlank()
                Guatemala.country -> uiState.personalDocumentValue.isNotBlank() && (uiState.personalDocumentValue.length == Guatemala.documentSize) && !uiState.personalIdError.first && uiState.firstNameValue.isNotBlank() && uiState.firstLastNameValue.isNotBlank()
                CostaRicaId.country -> uiState.personalDocumentValue.isNotBlank() &&
                        (uiState.personalDocumentValue.length == CostaRicaId.documentSize || uiState.personalDocumentValue.length == CostaRicaDimex.documentSize) &&
                        !uiState.personalIdError.first && uiState.identificationValueType.isNotBlank() && uiState.dataInformationClient?.fullName != null
                else -> false
            }
        )
    )

    private fun getCountry(
        nationality: String,
        updateNationality: (nationality: String, idBrand: Int) -> Unit,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
    ): String? {
        onSuccessCountry?.countryList?.forEach {
            if (it.countryPrefix?.lowercase() == nationality.lowercase()) {
                setDefaultCountry(nationality, updateNationality, onLoadingValueChange)
                return it.countryDescription
            }
        }
        return ""
    }

    private fun setDefaultCountry(
        nationality: String,
        updateNationality: (nationality: String, idBrand: Int) -> Unit,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
    ) {
        callQueryCatalogDocumentType(
            onSuccessCountry?.countryList?.find { it.countryPrefix == nationality }?.idBrand ?: 0,
            onLoadingValueChange
        )
        updateNationality.invoke(
            onSuccessCountry?.countryList?.find { it.countryPrefix == nationality }?.countryPrefix
                ?: "",
            onSuccessCountry?.countryList?.find { it.countryPrefix == nationality }?.idBrand ?: 0
        )
    }

    private fun getDocumentLength(documentType: String) {
        onSuccessCatalogDocumentType?.catalogDocument?.forEach { documentCatalog ->
            if (documentCatalog.description == documentType) {
                uiState = uiState.copy(
                    identificationIdSelected = documentCatalog.idDocument,
                    documentFormat = documentCatalog.format,
                    identificationValueType = documentType
                )
                documentLength =
                    documentCatalog.format.count { documentCatalog.format.last() == it }
            }
        }
    }

    private fun validateCrDocument(
        user: String,
    ): Pair<Boolean, Int> {
        val status = validId(
            if (uiState.identificationValueType == CrDocuments.IdDocument.document) CostaRicaId.documentSize else CostaRicaDimex.documentSize,
            R.string.sign_up_personal_data_id_not_valid,
            uiState.personalDocumentValue.length
        )
        if (status.first.not()) {
            closeKeyboard = true
            callQueryDataInformationClient(
                uiState.personalDocumentValue,
                onSuccessCountry?.countryList?.get(uiState.countryList.indexOf(uiState.nationalityValue))?.idBrand
                    ?: 0,
                user
            )
        } else {
            if (uiState.dataInformationClient?.fullName.isNullOrBlank().not()) {
                uiState = uiState.copy(dataInformationClient = null)
            }
        }
        return status
    }

    private fun callQueryDataInformationClient(
        identification: String,
        idBrand: Int,
        user: String,
    ) {
        viewModelScope.launch {
            queryDataInformationClientUseCase.invoke(
                identification,
                idBrand,
                user
            ).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(dataInformationClient = it)
                    isLoading = false
                    isFormValid()
                }
                result.onFailure {
                    isLoading = false
                    uiState = uiState.copy(
                        dataInformationClient = null,
                        personalIdError = Pair(
                            true,
                            R.string.sign_up_personal_data_id_not_valid
                        )
                    )
                    isFormValid()
                }
                result.onLoading {
                    isLoading = true
                }
            }
        }
    }

    private fun callQueryCatalogDocumentType(
        idBrand: Int,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            queryCatalogDocumentTypeUseCase.invoke(
                idBrand,
                ""
            ).collectLatest { result ->
                result.onSuccess {
                    onSuccessCatalogDocumentType = it
                    val documentList: ArrayList<String> = arrayListOf()
                    it?.catalogDocument?.forEach { document ->
                        documentList.add(document.description)
                    }
                    uiState = uiState.copy(documentList = documentList,
                        identificationValueType = if (documentList.size == SINGLE_DOCUMENT) documentList.first() else "")
                    if (uiState.identificationValueType.isEmpty().not()) {
                        getDocumentLength(uiState.identificationValueType)
                    }
                    onLoadingValueChange(false)
                }
                result.onFailure {
                    onLoadingValueChange(false)
                    onSuccessCatalogDocumentType = null
                }
                result.onLoading {
                    onLoadingValueChange(true)
                }
            }
        }
    }

    private fun callQueryGetCountryUseCase(
        user: String,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            queryGetCountryUseCase.invoke(user).collectLatest { result ->
                result.onSuccess {
                    onSuccessCountry = it
                    val countryList: ArrayList<String> = arrayListOf()
                    it?.countryList?.forEach { country ->
                        countryList.add(country.countryDescription ?: "")
                    }
                    uiState = uiState.copy(countryList = countryList)
                    emitBaseEvent(OnGetCountriesSuccess)
                    onLoadingValueChange(false)
                }
                result.onFailure {
                    onLoadingValueChange(false)
                    onSuccessCountry = null
                }
                result.onLoading {
                    onLoadingValueChange(true)
                }
            }
        }
    }

    private fun onNationalityChange(
        nationality: Int,
        updateNationality: (nationality: String, idBrand: Int) -> Unit,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
    ) {
        uiState = uiState.copy(
            nationalityValue = onSuccessCountry?.countryList?.get(nationality)?.countryDescription
                ?: "",
            personalDocumentValue = ""
        )
        callQueryCatalogDocumentType(
            onSuccessCountry?.countryList?.get(nationality)?.idBrand ?: 0,
            onLoadingValueChange
        )
        cleanUI()
        updateNationality.invoke(
            onSuccessCountry?.countryList?.get(nationality)?.countryPrefix ?: "",
            onSuccessCountry?.countryList?.get(nationality)?.idBrand ?: 0
        )
    }

    private fun onCallMutationUserValidationUseCase(email: String, nextStep: String, idBrand: Int) =
        executeUseCase {
            mutationUserValidationUseCase(
                email = email,
                currentStep = nextStep,
                idBrand = idBrand,
                idDocument = uiState.identificationIdSelected,
                identification = uiState.personalDocumentValue,
                firstName = uiState.firstNameValue,
                secondName = uiState.secondNameValue,
                firstSurname = uiState.firstLastNameValue,
                secondSurname = uiState.secondLastNameValue
            ).collectLatest { result ->
                onUserDataValidationEvent.emit(result)
            }
        }

    private fun onStart(
        nationality: String,
        identificationValue: String,
        identificationType: String,
        firstName: String,
        secondName: String,
        firstLastName: String,
        secondLastName: String,
        fullName: String,
        updateNationality: (nationality: String, idBrand: Int) -> Unit,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
    ) {
        val countryValue = getCountry(nationality, updateNationality, onLoadingValueChange) ?: ""
        uiState = uiState.copy(
            nationalityValue = countryValue,
            identificationValueType = identificationType,
            personalDocumentValue = identificationValue,
            firstNameValue = firstName,
            secondNameValue = secondName,
            firstLastNameValue = firstLastName,
            secondLastNameValue = secondLastName,
            fullNameValue = fullName,
            dataInformationClient = uiState.dataInformationClient?.copy(fullName = fullName)
        )
        isFormValid()
    }

    private fun cleanUI() {
        uiState = uiState.copy(
            identificationValueType = "",
            personalIdError = Pair(false, R.string.sign_up_personal_data_id_sv_required),
            nameError = Pair(false, R.string.sign_up_personal_data_id_sv_required),
            lastNameError = Pair(false, R.string.sign_up_personal_data_id_sv_required),
            personalDocumentValue = "",
            firstNameValue = "",
            secondNameValue = "",
            firstLastNameValue = "",
            secondLastNameValue = "",
            closeKeyboard = false
        )
    }

    private fun onIdentificationTypeValueChange(documentType: String) {
        getDocumentLength(documentType)
    }

    private fun onIdentificationValueChange(
        identificationValue: String,
        identificationShareViewModelChange: () -> Unit,
    ) {
        if (identificationValue.length <= documentLength) {
            uiState = uiState.copy(personalDocumentValue = identificationValue)
            identificationShareViewModelChange()
        }
    }

    private fun onFirstNameValueChange(
        firstName: String,
        onSharedViewModelFirstNameChange: () -> Unit,
    ) {
        uiState = uiState.copy(firstNameValue = firstName)
        onSharedViewModelFirstNameChange.invoke()
        isFormValid()
    }

    private fun onSecondNameValueChange(
        secondName: String,
        onSharedViewModelSecondNameChange: () -> Unit,
    ) {
        uiState = uiState.copy(secondNameValue = secondName)
        onSharedViewModelSecondNameChange.invoke()
        isFormValid()
    }

    private fun onFirstLastNameValueChange(
        firstLastName: String,
        onSharedViewModelFirstLastNameChange: () -> Unit,
    ) {
        uiState = uiState.copy(firstLastNameValue = firstLastName)
        onSharedViewModelFirstLastNameChange.invoke()
        isFormValid()
    }

    private fun onSecondLastNameValueChange(
        secondLastName: String,
        onSharedViewModelSecondLastNameChange: () -> Unit,
    ) {
        uiState = uiState.copy(secondLastNameValue = secondLastName)
        onSharedViewModelSecondLastNameChange.invoke()
        isFormValid()
    }

    fun getFullName(): String =
        "${uiState.firstNameValue} ${uiState.secondNameValue} ${uiState.firstLastNameValue} ${uiState.secondLastNameValue}"


    private fun onNextActionClick(
        email: String, nextStep: String, idBrand: Int,
    ) {
        onCallMutationUserValidationUseCase(email, nextStep, idBrand)
    }

    private fun validateDocument(email: String?) {
        uiState = uiState.copy(
            personalIdError = when (uiState.nationalityValue) {
                CostaRicaId.country -> validateCrDocument(email ?: "")
                ElSalvador.country -> validDui(uiState.personalDocumentValue)
                else -> validId(
                    Guatemala.documentSize,
                    R.string.sign_up_personal_data_dpi_gt_not_valid,
                    uiState.personalDocumentValue.length
                )
            }
        )
        isFormValid()
    }

    private fun onUserDataValidationSuccess(
        onUseDataValueChange: () -> Unit,
        onCallMutationUpdateUserRegisterUseCase: () -> Unit,
    ) {
        onUseDataValueChange()
        onCallMutationUpdateUserRegisterUseCase()
    }

    data class UIState(
        val documentFormat: String = "",
        val countryList: ArrayList<String> = arrayListOf(),
        val documentList: ArrayList<String> = arrayListOf(),
        val nationalityValue: String = "",
        val identificationIdSelected: Int = 0,
        val personalDocumentValue: String = "",
        val identificationValueType: String = "",
        val firstNameValue: String = "",
        val secondNameValue: String = "",
        val firstLastNameValue: String = "",
        val secondLastNameValue: String = "",
        val fullNameValue: String = "",
        val dataInformationClient: ClientInfoCr? = null,
        val personalIdError: Pair<Boolean, Int> = Pair(
            false,
            R.string.sign_up_personal_data_id_sv_required
        ),

        val nameError: Pair<Boolean, Int> = Pair(false, 0),
        val lastNameError: Pair<Boolean, Int> = Pair(false, 0),
        val closeKeyboard: Boolean = false,
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnStart -> onStart(
                event.nationality,
                event.identificationValue,
                event.identificationType,
                event.firstName,
                event.secondName,
                event.firstLastName,
                event.secondLastName,
                event.fullName,
                event.updateNationality,
                event.onLoadingValueChange
            )
            is OnNationalityChange -> onNationalityChange(
                event.nationality,
                event.updateNationality,
                event.onLoadingValueChange
            )
            is OnFirstNameChange -> onFirstNameValueChange(
                event.firstName,
                event.onSharedViewModelFirstNameChange
            )
            is OnSecondNameChange -> onSecondNameValueChange(
                event.secondName,
                event.onSharedViewModelSecondNameChange
            )
            is OnFirstLastNameChange -> onFirstLastNameValueChange(
                event.firstLastName,
                event.onSharedViewModelFirstLastNameChange
            )
            is OnSecondLastNameChange -> onSecondLastNameValueChange(
                event.secondLastName,
                event.onSharedViewModelSecondLastNameChange
            )
            is OnIdentificationTypeValueChange -> onIdentificationTypeValueChange(event.identificationType)
            is OnIdentificationValueChange -> onIdentificationValueChange(
                event.identification,
                event.identificationShareViewModelChange
            )
            is OnNextActionClick -> onNextActionClick(event.email, event.nextStep, event.idBrand)
            is OnUserDataValidationSuccess -> onUserDataValidationSuccess(
                event.onUseDataValueChange,
                event.onCallMutationUpdateUserRegisterUseCase,
            )
            is OnValidateDocument -> validateDocument(event.email)
            is OnCallQueryGetCountry -> callQueryGetCountryUseCase(event.user,
                event.onLoadingValueChange)
            is UIEvent.OnValidateForm -> isFormValid()
        }
    }

    sealed class UIEvent {
        data class OnStart(
            val nationality: String,
            val identificationValue: String,
            val identificationType: String,
            val firstName: String,
            val secondName: String,
            val firstLastName: String,
            val secondLastName: String,
            val fullName: String,
            val updateNationality: (nationality: String, idBrand: Int) -> Unit,
            val onLoadingValueChange: (isLoading: Boolean) -> Unit,
        ) : UIEvent()

        data class OnNationalityChange(
            val nationality: Int,
            val updateNationality: (nationality: String, idBrand: Int) -> Unit,
            val onLoadingValueChange: (isLoading: Boolean) -> Unit,
        ) :
            UIEvent()

        data class OnFirstNameChange(
            val firstName: String,
            val onSharedViewModelFirstNameChange: () -> Unit,
        ) : UIEvent()

        data class OnSecondNameChange(
            val secondName: String,
            val onSharedViewModelSecondNameChange: () -> Unit,
        ) : UIEvent()

        data class OnFirstLastNameChange(
            val firstLastName: String,
            val onSharedViewModelFirstLastNameChange: () -> Unit,
        ) : UIEvent()

        data class OnSecondLastNameChange(
            val secondLastName: String,
            val onSharedViewModelSecondLastNameChange: () -> Unit,
        ) : UIEvent()

        data class OnIdentificationTypeValueChange(val identificationType: String) : UIEvent()
        data class OnIdentificationValueChange(
            val identification: String,
            val identificationShareViewModelChange: () -> Unit,
        ) : UIEvent()

        data class OnNextActionClick(
            val email: String,
            val nextStep: String,
            val idBrand: Int,
        ) : UIEvent()

        data class OnValidateDocument(
            val email: String? = null,
        ) : UIEvent()

        data class OnCallQueryGetCountry(
            val user: String,
            val onLoadingValueChange: (isLoading: Boolean) -> Unit,
        ) :
            UIEvent()

        data class OnUserDataValidationSuccess(
            val currentStep: Int,
            val userData: UserData?,
            val onUseDataValueChange: () -> Unit,
            val onCallMutationUpdateUserRegisterUseCase: () -> Unit,
            val onMoveToStep: (step: Int) -> Unit,
            val onLoadingValueChange: () -> Unit,
        ) : UIEvent()

        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
        object OnGetCountriesSuccess : BaseEvent()
    }

    companion object {
        const val DUI_VERIFICATION_MODULE = 10
        const val FORMAT_VALUE = '0'
        const val SINGLE_DOCUMENT = 1
        const val STEP_TO_MOVE = 4
    }
}