package com.multimoney.multimoney.presentation.ui.smart.origination.facta

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnCrGoPageOne
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnCrGoPageTwo
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsActivityOfArt15Change
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsPEPChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsTaxPayerChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsUSCitizenChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsUSTaxPayerChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnLoadCurrentStepData
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnValidateForm
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartFactaViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    private fun onIsPEPChange(condition: Boolean) {
        uiState = uiState.copy(isPEP = condition)
        onValidateForm()
    }

    private fun onIsUSCitizenChange(condition: Boolean) {
        uiState = uiState.copy(isUSCitizen = condition)
        onValidateForm()
    }

    private fun onIsActivityOfArt15Change(condition: Boolean) {
        uiState = uiState.copy(isActivityOfArt15 = condition)
        onValidateForm()
    }

    private fun onIsUSTaxPayerChange(condition: Boolean) {
        uiState = uiState.copy(isUSTaxPayer = condition)
        onValidateForm()
    }

    private fun onIsTaxPayerChange(condition: Boolean) {
        uiState = uiState.copy(isTaxPayer = condition)
        onValidateForm()
    }

    private fun onCrGoPageTwo() {
        uiState = uiState.copy(
            crPage = CR_PAGE_TWO
        )
        onValidateForm()
    }

    private fun onCrGoPageOne() {
        uiState = uiState.copy(
            crPage = CR_PAGE_ONE
        )
    }

    fun isFormValid(): Boolean {
        return when (uiState.idBrand) {
            Brand.CostaRica.id -> {
                uiState.isPEP != null &&
                    uiState.isUSTaxPayer != null &&
                    uiState.isActivityOfArt15 != null &&
                    uiState.isTaxPayer != null
            }
            Brand.ElSalvador.id -> {
                uiState.isPEP != null && uiState.isUSCitizen != null
            }
            else -> {
                false
            }
        }
    }

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(accountSmartData: AccountSmartData?) {
        accountSmartData?.let {
            uiState = uiState.copy(
                idBrand = it.idBrand,
                isPEP = it.isPEP,
                isUSCitizen = it.isUSCitizen,
                isActivityOfArt15 = it.isActivityOfArt15,
                isUSTaxPayer = it.isUSTaxPayer,
                isTaxPayer = it.isTaxPayer
            )
            onValidateForm()
        }
    }

    private fun onValidateForm() =
        emitBaseEvent(OnFormValidateCompleted(isFormValid()))

    data class UIState(
        val idBrand: Int = 0,
        val isPEP: Boolean? = null,
        val isUSCitizen: Boolean? = null,
        val isActivityOfArt15: Boolean? = null,
        val isUSTaxPayer: Boolean? = null,
        val isTaxPayer: Boolean? = null,
        val crPage: Int = CR_PAGE_ONE
    )

    sealed class UIEvent {
        data class OnIsPEPChange(val condition: Boolean, val idBrand: Int) : UIEvent()
        data class OnIsUSCitizenChange(val condition: Boolean, val idBrand: Int) : UIEvent()
        data class OnIsActivityOfArt15Change(val condition: Boolean, val idBrand: Int) : UIEvent()
        data class OnIsUSTaxPayerChange(val condition: Boolean, val idBrand: Int) : UIEvent()
        data class OnIsTaxPayerChange(val condition: Boolean, val idBrand: Int) : UIEvent()
        object OnCrGoPageOne : UIEvent()
        object OnCrGoPageTwo : UIEvent()
        data class OnValidateForm(val idBrand: Int) : UIEvent()
        data class OnLoadCurrentStepData(val accountSmartData: AccountSmartData?) : UIEvent()
    }

    fun onUiEvent(event: UIEvent) {
        when (event) {
            is OnIsPEPChange -> onIsPEPChange(event.condition)
            is OnIsUSCitizenChange -> onIsUSCitizenChange(event.condition)
            is OnIsActivityOfArt15Change -> onIsActivityOfArt15Change(event.condition)
            is OnIsTaxPayerChange -> onIsTaxPayerChange(event.condition)
            is OnIsUSTaxPayerChange -> onIsUSTaxPayerChange(event.condition)
            is OnCrGoPageOne -> onCrGoPageOne()
            is OnCrGoPageTwo -> onCrGoPageTwo()
            is OnValidateForm -> onValidateForm()
            is OnLoadCurrentStepData -> onLoadCurrentStepData(event.accountSmartData)
        }
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val CR_PAGE_ONE = 1
        const val CR_PAGE_TWO = 2
    }
}
