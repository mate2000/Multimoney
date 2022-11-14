package com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnCompanyNameValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnDateFirstJobValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnInitData
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnLoadCreditSteps
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.ui.credit.origination.jobinfo.JobInfoViewModel.UIEvent.OnValidForm
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.getFormatDateByString
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JobInfoViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set
    var idBrand = Brand.ElSalvador.id

    private fun onInitData(idBrand: Int) {
        this.idBrand = idBrand
    }

    private fun onValidForm() {
        emitBaseEvent(
            OnFormCompleted(
                if (idBrand == Brand.CostaRica.id) {
                    uiState.companyName.isNotEmpty() && uiState.date.isNotEmpty() && uiState.phoneNumber.isNotEmpty() && uiState.phoneNumber.length == PHONE_NUMBER_MAX_LENGTH && uiState.dateFirstJob.isNotEmpty()
                } else {
                    uiState.companyName.isNotEmpty() && uiState.date.isNotEmpty() && uiState.phoneNumber.isNotEmpty() && uiState.phoneNumber.length == PHONE_NUMBER_MAX_LENGTH
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
            idBrand,
            user,
            uiState.companyName,
            getFormatDateByString(
                uiState.date.replace(VISUAL_DATE_SYMBOL, DASH_SYMBOL),
                DATE_FORMAT,
                BACKEND_DATE_FORMAT
            ),
            uiState.phoneNumber,
            getFormatDateByString(
                uiState.dateFirstJob.replace(VISUAL_DATE_SYMBOL, DASH_SYMBOL),
                DATE_FORMAT,
                BACKEND_DATE_FORMAT
            )
        )
        nextStepAction()
    }

    data class UIState(
        val companyName: String = "",
        val date: String = "",
        val dateFirstJob: String = "",
        val phoneNumber: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnValidForm -> onValidForm()
            is OnInitData -> onInitData(uiEvent.idBrand)
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
        data class OnInitData(val idBrand: Int) : UIEvent()
        data class OnLoadCreditSteps(val list: List<CreditCatalog?>?) : UIEvent()
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
