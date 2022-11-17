package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelThreeUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.domain.model.accountsmart.Address
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnCompanyNameChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnGetUserData
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnProfessionChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnSalaryChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.formalsalariedsv.FormalSalariedSvViewModel.UIEvent.OnWorkingAddressChange
import com.multimoney.multimoney.presentation.util.DECIMAL_REGEX
import com.multimoney.multimoney.presentation.util.DESCRIPTION_MAX_LENGTH
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class FormalSalariedSvViewModel @Inject constructor(
    val addressLevelTwoUseCase: QueryAddressLevelTwoUseCase,
    val addressLevelThreeUseCase: QueryAddressLevelThreeUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var user = ""
    var idBrand = DEFAULT_ID_BRAND

    private fun getDivisionTwo() {
        executeUseCase {
            addressLevelTwoUseCase(
                user,
                idBrand,
                NOT_APPLICABLE
            ).collectLatest {
                it.onSuccess { addressList ->
                    uiState = uiState.copy(divisionTwoList = addressList?.addresses)
                }
                it.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
                it.onFailure { error ->
                    uiState = uiState.copy(
                        dialogParameters = DialogParameters(
                            description = error.getError() ?: "",
                            isActive = mutableStateOf(true)
                        ),
                        isLoading = false
                    )
                    onRequestError()
                }
            }
        }
    }

    private fun getDivisionThree() {
        executeUseCase {
            uiState.divisionTwoSelected?.code?.let { divTwo ->
                addressLevelThreeUseCase(
                    user = user,
                    idBrand = idBrand,
                    idAddressLevelOne = NOT_APPLICABLE,
                    idAddressLevelTwo = divTwo
                ).collectLatest {
                    it.onSuccess { addressList ->
                        uiState = uiState.copy(
                            divisionThreeList = addressList?.addresses,
                            isLoading = false
                        )
                    }
                    it.onLoading {
                        uiState = uiState.copy(isLoading = true)
                    }
                    it.onFailure { error ->
                        uiState = uiState.copy(
                            dialogParameters = DialogParameters(
                                description = error.getError() ?: "",
                                isActive = mutableStateOf(true)
                            ),
                            isLoading = false
                        )
                        onRequestError()
                    }
                }
            }
        }
    }

    private fun onDivisionTwoValueChange(divisionTwo: String?) {
        uiState = uiState.copy(
            divisionTwoSelected = uiState.divisionTwoList?.find { it?.name == divisionTwo },
            divisionThreeSelected = null,
            divisionThreeList = listOf()
        )
        getDivisionThree()
    }

    private fun onDivisionThreeValueChange(divisionThree: String?) {
        uiState = uiState.copy(
            divisionThreeSelected = uiState.divisionThreeList?.find { it?.name == divisionThree }
        )
        onValidateForm()
    }

    private fun onCompanyNameChange(companyName: String) {
        uiState = uiState.copy(companyName = companyName)
        onValidateForm()
    }

    private fun onProfessionChange(profession: String) {
        uiState = uiState.copy(profession = profession)
        onValidateForm()
    }

    private fun onSalaryChange(salary: String) {
        if (Pattern.matches(DECIMAL_REGEX, salary) || salary.isEmpty()) {
            uiState = uiState.copy(salary = salary)
        }
        onValidateForm()
    }

    private fun onWorkingAddressChange(address: String) {
        uiState = if (address.length < DESCRIPTION_MAX_LENGTH) {
            uiState.copy(
                workingAddress = address,
                workingAddressError = Pair(false, R.string.empty)
            )
        } else {
            uiState.copy(
                workingAddressError = Pair(true, R.string.smart_own_business_description_max_char_error)
            )
        }
        onValidateForm()
    }

    private fun onValidateForm() = emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))
    private fun onRequestError() = emitBaseEvent(BaseEvent.OnRequestError(uiState.dialogParameters))

    fun isFormValid(): Boolean =
        uiState.profession.isNotBlank() &&
            uiState.companyName.isNotBlank() &&
            uiState.salary.isNotBlank() &&
            uiState.workingAddress.isNotBlank() &&
            uiState.divisionTwoSelected != null &&
            uiState.divisionThreeSelected != null

    data class UIState(
        var companyName: String = "",
        var profession: String = "",
        var salary: String = "",
        var workingAddress: String = "",
        var workingAddressError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val divisionTwoList: List<Address?>? = listOf(),
        val divisionThreeList: List<Address?>? = listOf(),
        val divisionTwoSelected: Address? = null,
        val divisionThreeSelected: Address? = null,
        val isLoading: Boolean = false,
        val dialogParameters: DialogParameters = DialogParameters()
    )

    sealed class UIEvent {
        data class OnCompanyNameChange(val companyName: String) : UIEvent()
        data class OnProfessionChange(val profession: String) : UIEvent()
        data class OnSalaryChange(val salary: String) : UIEvent()
        data class OnWorkingAddressChange(val address: String) : UIEvent()
        data class OnDivisionTwoValueChange(val divisionTwo: String?) : UIEvent()
        data class OnDivisionThreeValueChange(val divisionThree: String?) : UIEvent()
        data class OnGetUserData(val user: String, val idBrand: Int) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    fun onUiEvent(event: UIEvent) {
        when (event) {
            is OnGetUserData -> {
                user = event.user
                idBrand = event.idBrand
                getDivisionTwo()
            }
            is OnCompanyNameChange -> onCompanyNameChange(event.companyName)
            is OnProfessionChange -> onProfessionChange(event.profession)
            is OnSalaryChange -> onSalaryChange(event.salary)
            is OnWorkingAddressChange -> onWorkingAddressChange(event.address)
            is OnDivisionTwoValueChange -> onDivisionTwoValueChange(event.divisionTwo)
            is OnDivisionThreeValueChange -> onDivisionThreeValueChange(event.divisionThree)
            is OnValidateForm -> onValidateForm()
        }
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
        data class OnRequestError(val dialogParameters: DialogParameters) : BaseEvent()
    }

    companion object {
        const val NOT_APPLICABLE = "NA"
        const val DEFAULT_ID_BRAND = 0
    }
}
