package com.multimoney.multimoney.presentation.ui.smart.origination.facta

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnCrGoPageOne
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnCrGoPageTwo
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsActivityOfArt15Change
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsPEPChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsTaxPayerChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsUSCitizenChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnIsUSTaxPayerChange
import com.multimoney.multimoney.presentation.ui.smart.origination.facta.SmartFactaViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartFactaViewModel @Inject constructor() : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    data class UIState constructor(
        var isPEP: Boolean? = null,
        var isUSCitizen: Boolean? = null,
        var isActivityOfArt15: Boolean? = null,
        var isUSTaxPayer: Boolean? = null,
        var isTaxPayer: Boolean? = null,
        var crPage: Int = CR_PAGE_ONE,
        var openDialog: DialogParameters = DialogParameters()
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
    }

    fun onUiEvent(event: UIEvent) {
        when (event) {
            is OnIsPEPChange -> onIsPEPChange(event.condition, event.idBrand)
            is OnIsUSCitizenChange -> onIsUSCitizenChange(event.condition, event.idBrand)
            is OnIsActivityOfArt15Change -> onIsActivityOfArt15Change(event.condition, event.idBrand)
            is OnIsTaxPayerChange -> onIsTaxPayerChange(event.condition, event.idBrand)
            is OnIsUSTaxPayerChange -> onIsUSTaxPayerChange(event.condition, event.idBrand)
            is OnCrGoPageOne -> onCrGoPageOne()
            is OnCrGoPageTwo -> onCrGoPageTwo()
            is OnValidateForm -> onValidateForm(event.idBrand)
        }
    }

    private fun onIsPEPChange(condition: Boolean, idBrand: Int) {
        uiState = uiState.copy(isPEP = condition)
        onValidateForm(idBrand)
    }

    private fun onIsUSCitizenChange(condition: Boolean, idBrand: Int) {
        uiState = uiState.copy(isUSCitizen = condition)
        onValidateForm(idBrand)
    }

    private fun onIsActivityOfArt15Change(condition: Boolean, idBrand: Int) {
        uiState = uiState.copy(isActivityOfArt15 = condition)
        onValidateForm(idBrand)
    }

    private fun onIsUSTaxPayerChange(condition: Boolean, idBrand: Int) {
        uiState = uiState.copy(isUSTaxPayer = condition)
        onValidateForm(idBrand)
    }

    private fun onIsTaxPayerChange(condition: Boolean, idBrand: Int) {
        uiState = uiState.copy(isTaxPayer = condition)
        onValidateForm(idBrand)
    }

    private fun onCrGoPageTwo() {
        uiState = uiState.copy(
            crPage = CR_PAGE_TWO,
            isUSTaxPayer = null,
            isTaxPayer = null
        )
        onValidateForm(ID_BRAND_CR)
    }

    private fun onCrGoPageOne() {
        uiState = uiState.copy(
            crPage = CR_PAGE_ONE,
            isActivityOfArt15 = null,
            isPEP = null
        )
    }

    fun isFormValid(idBrand: Int): Boolean {
        return when (idBrand) {
            5 -> {
                uiState.isPEP != null &&
                    uiState.isUSTaxPayer != null &&
                    uiState.isActivityOfArt15 != null &&
                    uiState.isTaxPayer != null
            }
            7 -> {
                uiState.isPEP != null && uiState.isUSCitizen != null
            }
            else -> {
                false
            }
        }
    }

    private fun onValidateForm(idBrand: Int) = emitBaseEvent(OnFormValidateCompleted(isFormValid(idBrand)))

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}

const val CR_PAGE_ONE = 1
const val CR_PAGE_TWO = 2
