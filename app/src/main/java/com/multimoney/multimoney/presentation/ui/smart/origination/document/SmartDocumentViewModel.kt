package com.multimoney.multimoney.presentation.ui.smart.origination.document

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Gender
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.domain.interaction.accountsmart.QueryCivilStatusUseCase
import com.multimoney.domain.interaction.accountsmart.QueryNationalitiesUseCase
import com.multimoney.domain.interaction.accountsmart.QueryProfessionUseCase
import com.multimoney.domain.model.accountsmart.AddressLevelTwo
import com.multimoney.domain.model.accountsmart.CivilStatus
import com.multimoney.domain.model.accountsmart.Nationality
import com.multimoney.domain.model.accountsmart.Profession
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
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
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartDocumentViewModel @Inject constructor(
    private val queryCivilStatusUseCase: QueryCivilStatusUseCase,
    private val queryProfessionUseCase: QueryProfessionUseCase,
    private val queryNationalitiesUseCase: QueryNationalitiesUseCase,
    private val queryAddressLevelTwoUseCase: QueryAddressLevelTwoUseCase,
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    private fun callQueryNationalitiesUseCase(user: String = "401920903", idBrand: Int = 5) =
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
        user: String = "401920903",
        idBrand: Int = 5,
        idAddressLevelOne: String = "1",
    ) = executeUseCase {
        queryAddressLevelTwoUseCase.invoke(
            user = user,
            idBrand = idBrand,
            idAddressLevelOne = idAddressLevelOne
        ).collectLatest { result ->
            result.onSuccess { addresses ->
                addresses?.let {
                    val addressesString = it.addresses.map { address -> address?.name ?: "" }
                    uiState = uiState.copy(
                        addressLevelTwoList = it.addresses,
                        addressLevelTwoStringList = addressesString
                    )
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

    private fun callQueryCivilStatusUseCase(user: String = "401920903", idBrand: Int = 5) =
        executeUseCase {
            queryCivilStatusUseCase.invoke(
                user = user,
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { civilStatus ->
                    civilStatus?.let {
                        val civilStatusStrings =
                            it.status.map { status -> status?.maritalStatusDescription ?: "" }
                        uiState = uiState.copy(
                            civilStatusList = it.status,
                            civilStatusStringList = civilStatusStrings
                        )
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

    private fun callQueryProfessionUseCase(user: String = "40192", idBrand: Int = 5) =
        executeUseCase {
            queryProfessionUseCase.invoke(
                user = user,
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { successfulResult ->
                    val professionList = arrayListOf<String>()
                    successfulResult?.let {
                        it.status.map { professionStatus -> professionStatus?.name ?: "" }
                        uiState = uiState.copy(
                            professionList = it.status,
                            professionStringList = professionList
                        )
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

    private fun onExpirationDateValueChange(expirationDate: String) {
        uiState = uiState.copy(expirationDate = expirationDate)
        validateForm()
    }

    private fun onBirthDateValueChange(birthdate: String) {
        uiState = uiState.copy(birthdate = birthdate)
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
        val professionId = uiState.professionList.find { it?.name == profession }?.id
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
                        uiState.expirationDate.isNotBlank()
            )
        )
    }

    private fun onNextActionClick(nextStepAction: () -> Unit) {
        nextStepAction()
    }

    data class UIState(
        // Fields
        val expirationDate: String = "",
        val birthdate: String = "",
        val gender: String = "",
        val genderId: Long = 1,
        val civilState: String = "",
        val civilStateId: Long = 0,
        val profession: String = "",
        val professionId: Int = 0,
        val openDialog: DialogParameters = DialogParameters(),
        val nationalitiesList: List<Nationality?> = listOf(),
        val addressLevelTwoList: List<AddressLevelTwo?> = listOf(),
        val civilStatusList: List<CivilStatus?> = listOf(),
        val professionList: List<Profession?> = listOf(),
        val nationalitiesStringList: List<String> = listOf(),
        val addressLevelTwoStringList: List<String> = listOf(),
        val civilStatusStringList: List<String> = listOf(),
        val professionStringList: List<String> = listOf(),
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnGenderChange -> onGenderChange(event.gender)
            is OnCivilStateChange -> onCivilStateChange(event.civilState)
            is OnProfessionChange -> onProfessionChange(event.profession)
            is OnExpirationDateValueChange -> onExpirationDateValueChange(expirationDate = event.date)
            is OnBirthDateValueChange -> onBirthDateValueChange(event.date)
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
        }
    }

    sealed class UIEvent {
        data class OnStart(
            val userCompletedDialogDescription: String,
            val linkWhatsapp: String,
            val blockedMessage: String,
        ) : UIEvent()

        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnBirthDateValueChange(val date: String) : UIEvent()
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
            val idBrand: Int,
        ) : UIEvent()

        data class OnCallQueryCivilStatusUseCase(val user: String, val idBrand: Int) : UIEvent()
        data class OnCallQueryProfessionUseCase(val user: String, val idBrand: Int) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val BIRTH_DATE_MIN_YEAR = 1902
        const val BIRTH_DATE_MIN_MONTH = 0
        const val BIRTH_DATE_MIN_DAY = 1
    }
}
