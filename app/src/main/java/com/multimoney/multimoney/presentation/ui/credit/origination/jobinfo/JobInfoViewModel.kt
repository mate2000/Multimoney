package com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryProfessionsUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.companyaddress.CompanyAddressViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnCompanyNameValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnDateFirstJobValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnDivisionProfessionValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnInitData
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.HYPHEN
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getFormatDateByString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class JobInfoViewModel @Inject constructor(
    private val queryProfessionsUseCase: QueryProfessionsUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var idBrand = Brand.ElSalvador.id
    var isCrosseling = false
    private var profession: CreditCatalog? = null
    private var professionList: List<CreditCatalogOption?>? = listOf()

    private fun onInitData(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit,
        isCrosseling: Boolean
    ) {
        this.idBrand = idBrand
        this.isCrosseling = isCrosseling
        if (isCrosseling) {
            onCallQueryProfession(
                pkUser = pkUser,
                user = user,
                idBrand = idBrand,
                idUserRequest = idUserRequest,
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
                        filter?.description != HYPHEN
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
                        onDivisionProfessionValueChange(selectedProfession)

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

    private fun onValidForm() {
        emitBaseEvent(
            OnFormCompleted(
                if (isCrosseling) {
                    if (idBrand == Brand.CostaRica.id) {
                        uiState.companyName.isNotEmpty() && uiState.date.isNotEmpty() && uiState.phoneNumber.isNotEmpty() && uiState.phoneNumber.length == PHONE_NUMBER_MAX_LENGTH && uiState.dateFirstJob.isNotEmpty() && uiState.divisionProfessionSelected != null
                    } else {
                        uiState.companyName.isNotEmpty() && uiState.date.isNotEmpty() && uiState.phoneNumber.isNotEmpty() && uiState.phoneNumber.length == PHONE_NUMBER_MAX_LENGTH && uiState.divisionProfessionSelected != null
                    }
                } else {
                    if (idBrand == Brand.CostaRica.id) {
                        uiState.companyName.isNotEmpty() && uiState.date.isNotEmpty() && uiState.phoneNumber.isNotEmpty() && uiState.phoneNumber.length == PHONE_NUMBER_MAX_LENGTH && uiState.dateFirstJob.isNotEmpty()
                    } else {
                        uiState.companyName.isNotEmpty() && uiState.date.isNotEmpty() && uiState.phoneNumber.isNotEmpty() && uiState.phoneNumber.length == PHONE_NUMBER_MAX_LENGTH
                    }
                }
            )
        )
    }

    private fun onDateValueChange(date: String) {
        uiState = uiState.copy(date = date.replace(DASH_SYMBOL, VISUAL_DATE_SYMBOL))
        onValidForm()
    }

    private fun onDateFirstJobValueChange(date: String) {
        uiState = uiState.copy(dateFirstJob = date.replace(DASH_SYMBOL, VISUAL_DATE_SYMBOL))
        onValidForm()
    }

    private fun onCompanyNameValueChange(companyName: String) {
        uiState = uiState.copy(companyName = companyName)
        onValidForm()
    }

    private fun onPhoneNumberValueChange(phoneNumber: String) {
        if (phoneNumber.length <= PHONE_NUMBER_MAX_LENGTH) {
            uiState = uiState.copy(phoneNumber = phoneNumber)
            onValidForm()
        }
    }

    private fun loadStepsInfo(list: List<CreditCatalog?>?) {
        val companyName = list?.find { it?.description == SaveCreditStepsHelper.COMPANY_NAME }
        companyName?.value?.let {
            uiState = uiState.copy(companyName = companyName.value ?: "")
            onCompanyNameValueChange(it)
        }
        val date = list?.find { it?.description == SaveCreditStepsHelper.STARTED_JOB_DATE }
        date?.value?.let {
            val dateParsed = getFormatDateByString(
                it,
                BACKEND_DATE_FORMAT,
                DATE_FORMAT
            )
            onDateValueChange(dateParsed)
        }

        val phoneNumber = list?.find { it?.description == SaveCreditStepsHelper.COMPANY_PHONE }
        phoneNumber?.value?.let {
            uiState = uiState.copy(phoneNumber = phoneNumber.value ?: "")
            onPhoneNumberValueChange(it)
        }

        val dateFirstJob = list?.find { it?.description == SaveCreditStepsHelper.STARTED_FIRST_JOB_DATE }
        dateFirstJob?.value?.let {
            val dateFirstJobParsed = getFormatDateByString(
                it,
                BACKEND_DATE_FORMAT,
                DATE_FORMAT
            )
            onDateFirstJobValueChange(dateFirstJobParsed)
        }
    }

    private fun onNexActionClick(
        user: String,
        nextStepAction: () -> Unit,
        saveCreditStepsHelper: SaveCreditStepsHelper
    ) {
        saveCreditStepsHelper.saveStepThree(
            idBrand = idBrand,
            user = user,
            companyName = uiState.companyName,
            startedJobDate = getFormatDateByString(
                uiState.date.replace(VISUAL_DATE_SYMBOL, DASH_SYMBOL),
                DATE_FORMAT,
                BACKEND_DATE_FORMAT
            ),
            companyPhone = uiState.phoneNumber,
            dateFirstJob = if (uiState.dateFirstJob.isNotEmpty()) {
                getFormatDateByString(
                    uiState.dateFirstJob.replace(VISUAL_DATE_SYMBOL, DASH_SYMBOL),
                    DATE_FORMAT,
                    BACKEND_DATE_FORMAT
                )
            } else {
                ""
            },
            isCrosseling = isCrosseling,
            profession = profession,
            professionSelected = uiState.divisionProfessionSelected
        )
        nextStepAction()
    }

    private fun onDivisionProfessionValueChange(
        divisionProfession: CreditCatalogOption?
    ) {
        uiState = uiState.copy(
            divisionProfessionSelected = divisionProfession
        )
        onValidForm()
    }

    data class UIState(
        val companyName: String = "",
        val date: String = "",
        val dateFirstJob: String = "",
        val phoneNumber: String = "",
        val divisionProfessionList: List<CreditCatalogOption?>? = listOf(),
        val divisionProfessionSelected: CreditCatalogOption? = null
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnValidForm -> onValidForm()
            is OnInitData -> onInitData(
                uiEvent.pkUser,
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.idUserRequest,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog,
                uiEvent.isCrosseling
            )
            is OnDateValueChange -> onDateValueChange(uiEvent.date)
            is OnDateFirstJobValueChange -> onDateFirstJobValueChange(uiEvent.date)
            is OnCompanyNameValueChange -> onCompanyNameValueChange(uiEvent.companyName)
            is OnPhoneNumberValueChange -> onPhoneNumberValueChange(uiEvent.phoneNumber)
            is OnNextActionClick -> onNexActionClick(
                uiEvent.user,
                uiEvent.nextStepAction,
                uiEvent.saveCreditStepsHelper
            )
            is OnLoadCreditSteps -> loadStepsInfo(uiEvent.list)
            is OnDivisionProfessionValueChange -> onDivisionProfessionValueChange(uiEvent.divisionProfession)
        }
    }

    sealed class UIEvent {
        data class OnNextActionClick(
            val user: String,
            val nextStepAction: () -> Unit,
            val saveCreditStepsHelper: SaveCreditStepsHelper
        ) : UIEvent()

        data class OnCompanyNameValueChange(val companyName: String) : UIEvent()
        data class OnDateValueChange(val date: String) : UIEvent()
        data class OnDateFirstJobValueChange(val date: String) : UIEvent()
        data class OnPhoneNumberValueChange(val phoneNumber: String) : UIEvent()
        object OnValidForm : UIEvent()
        data class OnInitData(
            val pkUser: String,
            val user: String,
            val idBrand: Int,
            val idUserRequest: Int,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit,
            val isCrosseling: Boolean
        ) : UIEvent()
        data class OnLoadCreditSteps(val list: List<CreditCatalog?>?) : UIEvent()
        data class OnDivisionProfessionValueChange(val divisionProfession: CreditCatalogOption?) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormCompleted(val isFormCompleted: Boolean) : BaseEvent()
    }

    companion object {
        const val DATE_FORMAT = "dd-MM-yyyy"
        const val BACKEND_DATE_FORMAT = "yyyy-MM-dd"
        const val JOB_DATE_MIN_YEAR = 1972
        const val JOB_DATE_MIN_MONTH = 0
        const val JOB_DATE_MIN_DAY = 1
        const val PHONE_NUMBER_MAX_LENGTH = 8
        const val VISUAL_DATE_SYMBOL = " | "
        const val DASH_SYMBOL = "-"
    }
}
