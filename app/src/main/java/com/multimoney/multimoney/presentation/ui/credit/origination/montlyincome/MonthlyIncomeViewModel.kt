package com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryEmissionPlaceUseCase
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
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDivisionDuiEmissionPlaceValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnIncomeValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDuiEmissionDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnDuiExpirationDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.montlyincome.MonthlyIncomeViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getFormatDateByString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MonthlyIncomeViewModel @Inject constructor(
    private val queryProfessionsUseCase: QueryProfessionsUseCase,
    private val queryOccupationUseCase: QueryOccupationUseCase,
    private val queryEmissionPlaceUseCase: QueryEmissionPlaceUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    var pkUser = ""
    var user = ""
    var idBrand = Brand.ElSalvador.id
    var idUserRequest: Int = 0
    var duiExpirationMinDate: LocalDate? = null
    private var profession: CreditCatalog? = null
    private var occupation: CreditCatalog? = null
    private var duiEmissionPlace: CreditCatalog? = null
    private var professionList: List<CreditCatalogOption?>? = listOf()
    private var occupationList: List<CreditCatalogOption?>? = listOf()
    private var duiEmissionPlaceList: List<CreditCatalogOption?>? = listOf()

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = when (idBrand) {
                Brand.ElSalvador.id -> R.string.credit_monthly_income_title_sv
                else -> R.string.credit_monthly_income_title
            }
        )
    }

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

    private fun onDivisionDuiEmissionPlaceValueChange(
        divisionDuiEmissionPlace: CreditCatalogOption?
    ) {
        uiState = uiState.copy(
            divisionDuiEmissionPlaceSelected = divisionDuiEmissionPlace
        )
        onValidForm()
    }

    private fun onDuiEmissionDateValueChange(date: String) {
        uiState = uiState.copy(duiEmissionDate = date.replace(
            MIDDLE_DASH,
            VISUAL_DATE_SYMBOL
        ))
        onValidForm()
    }

    private fun onDuiExpirationDateValueChange(date: String) {
        uiState = uiState.copy(duiExpirationDate = date.replace(
            MIDDLE_DASH,
            VISUAL_DATE_SYMBOL
        ))
        onValidForm()
    }

    private fun onValidForm() {
        emitBaseEvent(
            OnFormCompleted(
                when (idBrand) {
                    Brand.CostaRica.id -> uiState.income.isNotEmpty() && uiState.income.toDouble() > ZERO && uiState.divisionProfessionSelected != null && uiState.divisionOccupationSelected != null
                    Brand.ElSalvador.id -> uiState.income.isNotEmpty() && uiState.income.toDouble() > ZERO && uiState.divisionProfessionSelected != null && uiState.divisionDuiEmissionPlaceSelected != null && uiState.duiEmissionDate.isNotEmpty() && uiState.duiExpirationDate.isNotEmpty()
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
        getTextResources()
        duiExpirationMinDate = getCurrentDate()
        onCallQueryProfession(
            pkUser = pkUser,
            user = user,
            idBrand = idBrand,
            idUserRequest = idUserRequest,
            onLoadingValueChange = onLoadingValueChange,
            onFailureWithDialog = onFailureWithDialog
        )
        if (idBrand == Brand.CostaRica.id) {
            onCallQueryOccupation(
                pkUser = pkUser,
                user = user,
                idBrand = idBrand,
                idUserRequest = idUserRequest,
                onLoadingValueChange = onLoadingValueChange,
                onFailureWithDialog = onFailureWithDialog
            )
        }
        if (idBrand == Brand.ElSalvador.id) {
            onCallQueryDuiEmissionPlace(
                pkUser = pkUser,
                idUserRequest = idUserRequest,
                idBrand = idBrand,
                user = user,
                onLoadingValueChange = onLoadingValueChange,
                onFailureWithDialog = onFailureWithDialog
            )
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

    private fun onCallQueryDuiEmissionPlace(
        pkUser: String,
        idUserRequest: Int,
        idBrand: Int,
        user: String,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryEmissionPlaceUseCase.invoke(
            pkUser = pkUser.toInt(),
            idUserRequest = idUserRequest,
            idBrand = idBrand,
            user = user
        )
            .collectLatest { result ->
                result.onSuccess {
                    if (duiEmissionPlace == null) {
                        duiEmissionPlace = it?.first()
                    }
                    duiEmissionPlaceList = duiEmissionPlace?.subOptions?.filter { filter ->
                        filter?.description != MIDDLE_DASH
                    }

                    uiState = uiState.copy(
                        divisionDuiEmissionPlaceList = duiEmissionPlace?.subOptions?.filter { filter ->
                            filter?.description != MIDDLE_DASH
                        }
                    )
                    if (!duiEmissionPlace?.pkCatalog.isNullOrEmpty()) {
                        val selectedDuiEmissionPlace =
                            duiEmissionPlaceList?.find { it?.pkCatalog == duiEmissionPlace?.pkCatalog }
                        uiState = uiState.copy(divisionDuiEmissionPlaceSelected = selectedDuiEmissionPlace)
                        onUIEvent(
                            OnDivisionDuiEmissionPlaceValueChange(
                                selectedDuiEmissionPlace
                            )
                        )
                        duiEmissionPlace = duiEmissionPlace?.copy(pkCatalog = null)
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
            user = user,
            idBrand = idBrand,
            monthlyIncomeValue = uiState.income,
            profession = profession,
            professionSelected = uiState.divisionProfessionSelected,
            occupation = occupation,
            occupationSelected = uiState.divisionOccupationSelected,
            duiEmissionPlace = duiEmissionPlace,
            duiEmissionPlaceSelected = uiState.divisionDuiEmissionPlaceSelected,
            duiEmissionDateValue = getFormatDateByString(
                uiState.duiEmissionDate.replace(
                    VISUAL_DATE_SYMBOL,
                    MIDDLE_DASH
                ),
                DATE_FORMAT,
                BACKEND_DATE_FORMAT
            ),
            duiExpirationDateValue = getFormatDateByString(
                uiState.duiExpirationDate.replace(
                    VISUAL_DATE_SYMBOL,
                    MIDDLE_DASH
                ),
                DATE_FORMAT,
                BACKEND_DATE_FORMAT
            )
        )
        onNextStepAction()
    }

    data class UIState(
        val titleResource: Int = R.string.empty,
        val income: String = "",
        val duiEmissionDate: String = "",
        val duiExpirationDate: String = "",
        val incomeError: Pair<Boolean, Int> = Pair(
            false,
            R.string.credit_monthly_income_greater_than_zero_error
        ),
        val divisionProfessionList: List<CreditCatalogOption?>? = listOf(),
        val divisionProfessionSelected: CreditCatalogOption? = null,
        val divisionOccupationList: List<CreditCatalogOption?>? = listOf(),
        val divisionOccupationSelected: CreditCatalogOption? = null,
        val divisionDuiEmissionPlaceList: List<CreditCatalogOption?>? = listOf(),
        val divisionDuiEmissionPlaceSelected: CreditCatalogOption? = null
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
            is OnDivisionDuiEmissionPlaceValueChange -> {
                onDivisionDuiEmissionPlaceValueChange(
                    uiEvent.divisionDuiEmissionPlace
                )
            }
            is OnDuiEmissionDateValueChange -> {
                onDuiEmissionDateValueChange(uiEvent.date)
            }
            is OnDuiExpirationDateValueChange -> {
                onDuiExpirationDateValueChange(uiEvent.date)
            }
        }
    }

    private fun loadStepsInfo(list: List<CreditCatalog?>?) {
        val salary = list?.find { it?.description == SaveCreditStepsHelper.SALARY }
        uiState = uiState.copy(income = salary?.value ?: "")
        val duiEmissionDate = list?.find { it?.description == SaveCreditStepsHelper.DUI_EMISSION_DATE }
        duiEmissionDate?.value?.let {
            val dateParsed = getFormatDateByString(
                it,
                BACKEND_DATE_FORMAT,
                DATE_FORMAT
            )
            onDuiEmissionDateValueChange(dateParsed)
        }
        val duiExpirationDate = list?.find { it?.description == SaveCreditStepsHelper.DUI_EXPIRATION_DATE }
        duiExpirationDate?.value?.let {
            val dateParsed = getFormatDateByString(
                it,
                BACKEND_DATE_FORMAT,
                DATE_FORMAT
            )
            onDuiExpirationDateValueChange(dateParsed)
        }
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

        data class OnDivisionDuiEmissionPlaceValueChange(
            val divisionDuiEmissionPlace: CreditCatalogOption?
        ) : UIEvent()

        data class OnDuiEmissionDateValueChange(val date: String) : UIEvent()
        data class OnDuiExpirationDateValueChange(val date: String) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormCompleted(val isFormCompleted: Boolean) : BaseEvent()
    }

    companion object {
        const val ZERO = 0
        const val MIDDLE_DASH = "-"
        const val VISUAL_DATE_SYMBOL = " | "
        const val DATE_FORMAT = "dd-MM-yyyy"
        const val BACKEND_DATE_FORMAT = "yyyy-MM-dd"
        const val DATE_MIN_YEAR = 0
        const val DATE_MIN_MONTH = 0
        const val DATE_MIN_DAY = 1
    }
}
