package com.multimoney.multimoney.presentation.ui.credit.jobplace

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.BaseEvent.OnFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.UIEvent.OnCompanyNameValueChange
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.UIEvent.OnDateValueChange
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.ui.credit.jobplace.JobPlaceViewModel.UIEvent.OnValidForm
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JobPlaceViewModel @Inject constructor() : BaseViewModel() {

    var uiState by mutableStateOf(UIState())
        private set

    private fun onValidForm() {
        emitBaseEvent(
            OnFormCompleted(
                uiState.companyName.isNotEmpty() && uiState.date.isNotEmpty() && uiState.phoneNumber.isNotEmpty()
            )
        )
    }

    private fun onDateValueChange(date: String) {
        uiState = uiState.copy(date = date.replace("-", " | "))
        onValidForm()
    }

    private fun onCompanyNameValueChange(companyName: String) {
        uiState = uiState.copy(companyName = companyName)
        onValidForm()
    }

    private fun onPhoneNumberValueChange(phoneNumber: String) {
        uiState = uiState.copy(phoneNumber = phoneNumber)
        onValidForm()
    }

    data class UIState(
        val companyName: String = "",
        val date: String = "",
        val phoneNumber: String = "",
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnValidForm -> onValidForm()
            is OnDateValueChange -> onDateValueChange(uiEvent.date)
            is OnCompanyNameValueChange -> onCompanyNameValueChange(uiEvent.companyName)
            is OnPhoneNumberValueChange -> onPhoneNumberValueChange(uiEvent.phoneNumber)
            is OnNextActionClick -> uiEvent.nextStepAction.invoke()
        }
    }

    sealed class UIEvent {
        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnCompanyNameValueChange(val companyName: String) : UIEvent()
        data class OnDateValueChange(val date: String) : UIEvent()
        data class OnPhoneNumberValueChange(val phoneNumber: String) : UIEvent()
        object OnValidForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormCompleted(val isFormCompleted: Boolean) : BaseEvent()
    }
}