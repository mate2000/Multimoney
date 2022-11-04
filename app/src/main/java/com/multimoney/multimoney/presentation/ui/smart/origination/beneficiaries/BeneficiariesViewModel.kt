package com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QueryRelationshipUseCase
import com.multimoney.domain.model.accountsmart.Relationship
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnAddBeneficiaryStateChance
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnBeneficiaryFullNameValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnCallQueryRelationshipUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnPercentageValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnRelationshipValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.ui.smart.origination.document.SmartDocumentViewModel.BaseEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class BeneficiariesViewModel @Inject constructor(private val queryRelationshipUseCase: QueryRelationshipUseCase) :
    BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())

    private fun callQueryRelationshipUseCase(user: String, idBrand: Int, idRequest: Int) =
        executeUseCase {
            queryRelationshipUseCase.invoke(user, idBrand, idRequest).collectLatest { result ->
                result.onSuccess { success ->
                    success?.data?.let {
                        uiState = uiState.copy(relationshipList = it)
                    }
                }
            }
        }

    fun onRelationShipValueChange(relationShip: String) {
        uiState =
            uiState.copy(relationship = relationShip)
        validateForm()
    }

    private fun validateForm() {
        emitBaseEvent(
            OnFormValidateCompleted(
                isFormValid = uiState.beneficiaryFullName.isNotBlank() && uiState.percentage.isNotBlank() && uiState.relationship.isNotBlank()
            )
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnCallQueryRelationshipUseCase -> callQueryRelationshipUseCase(
                event.user,
                event.idBrand,
                event.option
            )
            is OnRelationshipValueChange -> uiState =
                uiState.copy(relationship = event.relationship)
            is OnBeneficiaryFullNameValueChange -> uiState =
                uiState.copy(beneficiaryFullName = event.fullName)
            is OnPercentageValueChange -> uiState = uiState.copy(percentage = event.percentage)
            is OnNextActionClick -> event.nextStepAction()
            is OnValidateForm -> validateForm()
            is OnAddBeneficiaryStateChance -> uiState = uiState.copy(addBeneficiaryState = event.status)
        }
    }

    data class UIState(
        val relationshipList: List<Relationship?> = listOf(),
        val beneficiaryFullName: String = "",
        val relationship: String = "",
        val percentage: String = "",
        val addBeneficiaryState: Boolean = true
    )

    sealed class UIEvent {
        data class OnCallQueryRelationshipUseCase(
            val user: String,
            val idBrand: Int,
            val option: Int,
        ) : UIEvent()

        data class OnBeneficiaryFullNameValueChange(val fullName: String) : UIEvent()
        data class OnRelationshipValueChange(val relationship: String) : UIEvent()
        data class OnPercentageValueChange(val percentage: String) : UIEvent()
        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnAddBeneficiaryStateChance(val status: Boolean) : UIEvent()
        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}