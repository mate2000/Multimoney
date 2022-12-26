package com.multimoney.multimoney.presentation.ui.smart.origination.document

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Gender
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.domain.interaction.accountsmart.QueryCivilStatusUseCase
import com.multimoney.domain.interaction.accountsmart.QueryNationalitiesUseCase
import com.multimoney.domain.interaction.accountsmart.QueryProfessionUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.domain.model.accountsmart.Address
import com.multimoney.domain.model.accountsmart.CivilStatus
import com.multimoney.domain.model.accountsmart.Nationality
import com.multimoney.domain.model.accountsmart.ProfessionSmart
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnBirthDateValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnCallQueryAddressLevelTwoUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnCallQueryCivilStatusUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnCallQueryNationalitiesUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnCallQueryProfessionUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnCivilStateChange
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnExpirationDateValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnFailureWithDialog
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnGenderChange
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.API_DATE_FORMAT
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getDayFromString
import com.multimoney.multimoney.presentation.util.onBirthDateAgeValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SmartDocumentViewModel @Inject constructor(
    private val queryCivilStatusUseCase: QueryCivilStatusUseCase,
    private val queryProfessionUseCase: QueryProfessionUseCase,
    private val queryNationalitiesUseCase: QueryNationalitiesUseCase,
    private val queryAddressLevelTwoUseCase: QueryAddressLevelTwoUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun callQueryNationalitiesUseCase(user: String, idBrand: Int) =
        executeUseCase {
            queryNationalitiesUseCase.invoke(
                user = user,
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { nationalities ->
                    nationalities?.let {
                        uiState = uiState.copy(nationalitiesList = it.nationalityList)
                    }
                    onUIEvent(OnLoadingValueChange(false))
                }
                result.onFailure {
                    onUIEvent(
                        OnFailureWithDialog(
                            isLoading = false,
                            openDialog = DialogParameters(
                                description = it.getError() ?: "",
                                isActive = mutableStateOf(true)
                            )
                        )
                    )
                }
                result.onLoading {
                    onUIEvent(OnLoadingValueChange(true))
                }
            }
        }

    private fun callQueryAddressLevelTwoUseCase(
        user: String,
        idBrand: Int,
        idAddressLevelOne: String
    ) = executeUseCase {
        queryAddressLevelTwoUseCase.invoke(
            user = user,
            idBrand = idBrand,
            idAddressLevelOne = idAddressLevelOne
        ).collectLatest { result ->
            result.onSuccess { addresses ->
                uiState = uiState.copy(addressLevelTwoList = addresses?.addresses ?: emptyList())
                onUIEvent(OnLoadingValueChange(false))
            }
            result.onFailure {
                onUIEvent(
                    OnFailureWithDialog(
                        isLoading = false,
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                )
            }
            result.onLoading {
                onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    private fun callQueryCivilStatusUseCase(user: String, idBrand: Int) =
        executeUseCase {
            queryCivilStatusUseCase.invoke(
                user = user,
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { civilStatus ->
                    uiState = uiState.copy(civilStatusList = civilStatus?.status ?: emptyList())
                    onUIEvent(OnLoadingValueChange(false))
                }
                result.onFailure {
                    onUIEvent(
                        OnFailureWithDialog(
                            isLoading = false,
                            openDialog = DialogParameters(
                                description = it.getError() ?: "",
                                isActive = mutableStateOf(true)
                            )
                        )
                    )
                }
                result.onLoading {
                    onUIEvent(OnLoadingValueChange(true))
                }
            }
        }

    private fun callQueryProfessionUseCase(user: String, idBrand: Int) =
        executeUseCase {
            queryProfessionUseCase.invoke(
                user = user,
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { successfulResult ->
                    uiState =
                        uiState.copy(professionSmartList = successfulResult?.status ?: emptyList())
                    onUIEvent(OnLoadingValueChange(false))
                }
                result.onFailure {
                    onUIEvent(
                        OnFailureWithDialog(
                            isLoading = false,
                            openDialog = DialogParameters(
                                description = it.getError() ?: "",
                                isActive = mutableStateOf(true)
                            )
                        )
                    )
                }
                result.onLoading {
                    onUIEvent(OnLoadingValueChange(true))
                }
            }
        }

    private fun onExpirationDateValueChange(expirationDate: String) {
        uiState = uiState.copy(expirationDate = expirationDate)
        validateForm()
    }

    private fun onBirthDateValueChange(birthdate: String, pickedDate: LocalDate) {
        val dateValidation = onBirthDateAgeValidation(pickedDate)
        uiState = uiState.copy(
            birthdate = birthdate,
            birthdateErrorStatus = dateValidation.first,
            birthdateError = dateValidation.second
        )
        validateForm()
    }

    private fun onGenderChange(gender: String) {
        val genderId = Gender.Search.getGenderIdByName(gender)
        uiState = uiState.copy(gender = gender, genderId = genderId)
        validateForm()
    }

    private fun onCivilStateChange(civilState: String) {
        val civilStateId = uiState.civilStatusList.find {
            it?.maritalStatusDescription == civilState
        }?.maritalStatusId

        uiState = uiState.copy(civilState = civilState, civilStateId = civilStateId?.toLong() ?: 0)
        validateForm()
    }

    private fun onProfessionChange(profession: String) {
        val professionId = uiState.professionSmartList.find { it?.name == profession }?.id
        uiState = uiState.copy(profession = profession, professionId = professionId ?: 0)
        validateForm()
    }

    private fun validateForm() {
        emitBaseEvent(
            BaseEvent.OnFormValidateCompleted(
                isFormValid = uiState.gender.isNotBlank() &&
                    uiState.birthdate.isNotBlank() &&
                    uiState.civilState.isNotBlank() &&
                    uiState.profession.isNotBlank() &&
                    uiState.expirationDate.isNotBlank() && !uiState.birthdateErrorStatus
            )
        )
    }

    private fun onNextActionClick(nextStepAction: () -> Unit) {
        nextStepAction()
    }

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(accountSmartData: AccountSmartData?) {
        uiState = uiState.copy(
            birthdate = getDayFromString(accountSmartData?.birthday, API_DATE_FORMAT),
            expirationDate = getDayFromString(accountSmartData?.expirationDate, API_DATE_FORMAT)
        )
        onGenderChange(accountSmartData?.strGenre.orEmpty())
        onCivilStateChange(accountSmartData?.strMaritalStatus.orEmpty())
        onProfessionChange(accountSmartData?.stringProfessionType.orEmpty())
    }

    data class UIState(
        // Fields
        val expirationDate: String = "",
        val birthdate: String = "",
        val birthdateError: Int = R.string.smart_account_document_birthdate_age_error,
        val birthdateErrorStatus: Boolean = false,
        val gender: String = "",
        val genderId: Long = 1,
        val civilState: String = "",
        val civilStateId: Long = 0,
        val profession: String = "",
        val professionId: Int = 0,
        val openDialog: DialogParameters = DialogParameters(),
        val nationalitiesList: List<Nationality?> = listOf(),
        val addressLevelTwoList: List<Address?> = listOf(),
        val civilStatusList: List<CivilStatus?> = listOf(),
        val professionSmartList: List<ProfessionSmart?> = listOf()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnGenderChange -> onGenderChange(event.gender)
            is OnCivilStateChange -> onCivilStateChange(event.civilState)
            is OnProfessionChange -> onProfessionChange(event.profession)
            is OnExpirationDateValueChange -> onExpirationDateValueChange(expirationDate = event.date)
            is OnBirthDateValueChange -> onBirthDateValueChange(event.date, event.pickedDate)
            is OnCallQueryNationalitiesUseCase -> callQueryNationalitiesUseCase(
                event.user,
                event.idBrand
            )
            is OnCallQueryAddressLevelTwoUseCase -> callQueryAddressLevelTwoUseCase(
                event.user,
                event.idBrand,
                event.pkUser
            )
            is OnCallQueryCivilStatusUseCase -> callQueryCivilStatusUseCase(
                event.user,
                event.idBrand
            )
            is OnCallQueryProfessionUseCase -> callQueryProfessionUseCase(event.user, event.idBrand)
            is OnValidateForm -> validateForm()
            is OnNextActionClick -> onNextActionClick(event.nextStepAction)
            is UIEvent.OnLoadCurrentStepData -> onLoadCurrentStepData(event.accountSmartData)
        }
    }

    sealed class UIEvent {

        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnBirthDateValueChange(val date: String, val pickedDate: LocalDate) : UIEvent()
        data class OnExpirationDateValueChange(val date: String) : UIEvent()
        data class OnGenderChange(val gender: String) : UIEvent()
        data class OnCivilStateChange(val civilState: String) : UIEvent()
        data class OnProfessionChange(val profession: String) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnCallQueryNationalitiesUseCase(val user: String, val idBrand: Int) : UIEvent()
        data class OnCallQueryAddressLevelTwoUseCase(
            val user: String,
            val pkUser: String,
            val idBrand: Int
        ) : UIEvent()

        data class OnCallQueryCivilStatusUseCase(val user: String, val idBrand: Int) : UIEvent()
        data class OnCallQueryProfessionUseCase(val user: String, val idBrand: Int) : UIEvent()
        data class OnLoadCurrentStepData(
            val accountSmartData: AccountSmartData?
        ) : UIEvent()

        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}
