package com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryOccupationUseCase
import com.multimoney.domain.interaction.credit.QueryProfessionsUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.companyaddress.CompanyAddressViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnCallCatalogs
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDivisionOccupationValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDivisionProfessionValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnIncomeValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MonthlyIncomeViewModel @Inject constructor(
    private val queryProfessionsUseCase: QueryProfessionsUseCase,
    private val queryOccupationUseCase: QueryOccupationUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    var pkUser = ""
    var user = ""
    var idBrand = Brand.ElSalvador.id
    var idUserRequest: Int = 0
    private var profession: CreditCatalog? = null
    private var occupation: CreditCatalog? = null
    private var professionList: List<CreditCatalogOption?>? = listOf()
    private var occupationList: List<CreditCatalogOption?>? = listOf()

    private fun onIncomeValueChange(income: String) {
        if (income.isDigitsOnly()) {
            val incomeError = if (income.isNotEmpty() && income == ZERO.toString()) {
                Pair(
                    true,
                    R.string.credit_monthly_income_greater_than_zero_error
                )
            } else {
                Pair(
                    false,
                    R.string.credit_monthly_income_greater_than_zero_error
                )
            }
            uiState = uiState.copy(
                income = income,
                incomeError = incomeError
            )
            onValidForm()
        }
    }

    private fun onDivisionProfessionValueChange(
        divisionProfession: CreditCatalogOption?
    ) {
        uiState = uiState.copy(
            divisionProfessionSelected = divisionProfession
        )
        onValidForm()
    }

    private fun onDivisionOccupationValueChange(
        divisionOccupation: CreditCatalogOption?
    ) {
        uiState = uiState.copy(
            divisionOccupationSelected = divisionOccupation
        )
        onValidForm()
    }

    private fun onValidForm() {
        emitBaseEvent(
            OnFormCompleted(
                when (idBrand) {
                    Brand.CostaRica.id -> uiState.income.isNotEmpty() && uiState.income.toDouble() > ZERO && uiState.divisionProfessionSelected != null && uiState.divisionOccupationSelected != null
                    else -> uiState.income.isNotEmpty() && uiState.income.toDouble() > ZERO && uiState.divisionProfessionSelected != null
                }
            )
        )
    }

    private fun onCallCatalogs(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        this.pkUser = pkUser
        this.user = user
        this.idBrand = idBrand
        this.idUserRequest = idUserRequest
        onCallQueryProfession(pkUser, user, idBrand, idUserRequest, onLoadingValueChange, onFailureWithDialog)
        if (idBrand == Brand.CostaRica.id) {
            onCallQueryOccupation(pkUser, user, idBrand, idUserRequest, onLoadingValueChange, onFailureWithDialog)
        }
    }

    private fun onCallQueryProfession(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryProfessionsUseCase.invoke(pkUser.toInt(), user, idBrand, idUserRequest)
            .collectLatest { result ->
                result.onSuccess {
                    if (profession == null) {
                        profession = it?.first()
                    }
                    professionList = profession?.subOptions?.filter { filter ->
                        filter?.description != MIDDLE_DASH
                    }

                    uiState = uiState.copy(
                        divisionProfessionList = profession?.subOptions?.filter { filter ->
                            filter?.description != CompanyAddressViewModel.MIDDLE_DASH
                        }
                    )
                    if (!profession?.pkCatalog.isNullOrEmpty()) {
                        val selectedProfession =
                            professionList?.find { it?.pkCatalog == profession?.pkCatalog }
                        uiState = uiState.copy(divisionProfessionSelected = selectedProfession)
                        onUIEvent(
                            OnDivisionProfessionValueChange(
                                selectedProfession
                            )
                        )
                        profession = profession?.copy(pkCatalog = null)
                    }
                    onLoadingValueChange(false)
                }
                result.onLoading {
                    onLoadingValueChange(true)
                }
                result.onFailure {
                    onFailureWithDialog(
                        false,
                        DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                }
            }
    }

    private fun onCallQueryOccupation(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryOccupationUseCase.invoke(pkUser.toInt(), user, idBrand, idUserRequest)
            .collectLatest { result ->
                result.onSuccess {
                    if (occupation == null) {
                        occupation = it?.first()
                    }
                    occupationList = occupation?.subOptions?.filter { filter ->
                        filter?.description != MIDDLE_DASH
                    }

                    uiState = uiState.copy(
                        divisionOccupationList = occupation?.subOptions?.filter { filter ->
                            filter?.description != MIDDLE_DASH
                        }
                    )
                    if (!occupation?.pkCatalog.isNullOrEmpty()) {
                        val selectedOccupation =
                            occupationList?.find { it?.pkCatalog == occupation?.pkCatalog }
                        uiState = uiState.copy(divisionOccupationSelected = selectedOccupation)
                        onUIEvent(
                            OnDivisionOccupationValueChange(
                                selectedOccupation
                            )
                        )
                        occupation = occupation?.copy(pkCatalog = null)
                    }
                    onLoadingValueChange(false)
                }
                result.onLoading {
                    onLoadingValueChange(true)
                }
                result.onFailure {
                    onFailureWithDialog(
                        false,
                        DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                }
            }
    }

    private fun onNextActionClick(
        user: String,
        onNextStepAction: () -> Unit,
        saveCreditStepsHelper: SaveCreditStepsHelper
    ) {
        saveCreditStepsHelper.saveStepTwo(
            user,
            idBrand,
            uiState.income,
            profession,
            uiState.divisionProfessionSelected,
            occupation,
            uiState.divisionOccupationSelected
        )
        onNextStepAction()
    }

    data class UIState(
        val income: String = "",
        val incomeError: Pair<Boolean, Int> = Pair(
            false,
            R.string.credit_monthly_income_greater_than_zero_error
        ),
        val divisionProfessionList: List<CreditCatalogOption?>? = listOf(),
        val divisionProfessionSelected: CreditCatalogOption? = null,
        val divisionOccupationList: List<CreditCatalogOption?>? = listOf(),
        val divisionOccupationSelected: CreditCatalogOption? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNextActionClick -> onNextActionClick(
                uiEvent.user,
                uiEvent.nextStepAction,
                uiEvent.saveCreditStepsHelper
            )
            is OnIncomeValueChange -> onIncomeValueChange(uiEvent.income)
            is OnValidForm -> onValidForm()
            is OnLoadCreditSteps -> loadStepsInfo(uiEvent.list)
            is OnCallCatalogs -> onCallCatalogs(
                uiEvent.pkUser,
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.idUserRequest,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog
            )
            is OnDivisionProfessionValueChange -> {
                onDivisionProfessionValueChange(
                    uiEvent.divisionProfession
                )
            }
            is OnDivisionOccupationValueChange -> {
                onDivisionOccupationValueChange(
                    uiEvent.divisionOccupation
                )
            }
        }
    }

    private fun loadStepsInfo(list: List<CreditCatalog?>?) {
        val salary = list?.find { it?.description == SaveCreditStepsHelper.SALARY }
        uiState = uiState.copy(income = salary?.value ?: "")
    }

    sealed class UIEvent {
        data class OnNextActionClick(
            val user: String,
            val nextStepAction: () -> Unit,
            val saveCreditStepsHelper: SaveCreditStepsHelper
        ) : UIEvent()

        data class OnIncomeValueChange(val income: String) : UIEvent()
        object OnValidForm : UIEvent()
        data class OnLoadCreditSteps(val list: List<CreditCatalog?>?) : UIEvent()
        data class OnCallCatalogs(
            val pkUser: String,
            val user: String,
            val idBrand: Int,
            val idUserRequest: Int,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()

        data class OnDivisionProfessionValueChange(
            val divisionProfession: CreditCatalogOption?
        ) : UIEvent()

        data class OnDivisionOccupationValueChange(
            val divisionOccupation: CreditCatalogOption?
        ) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormCompleted(val isFormCompleted: Boolean) : BaseEvent()
    }

    companion object {
        const val ZERO = 0
        const val MIDDLE_DASH = "-"
    }
}
