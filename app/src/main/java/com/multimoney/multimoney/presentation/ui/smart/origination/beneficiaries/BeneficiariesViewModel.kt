package com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QueryRelationshipUseCase
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.accountsmart.Relationship
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnAddBeneficiaryStateChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnBeneficiaryFullNameValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnCallQueryRelationshipUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnPercentageValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnRelationshipValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
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

    private fun onFullNameValueChange(name: String) {
        uiState = uiState.copy(beneficiaryFullName = name)
        validateForm()
    }

    private fun onRelationShipValueChange(relationShip: String) {
        uiState = uiState.copy(relationship = relationShip)
        validateForm()
    }

    private fun onPercentageValueChange(percentage: String) {
        if (percentage.length <= 3) {
            uiState = uiState.copy(percentage = percentage)
        }
        validateForm()
    }

    private fun onAddBeneficiaryStateChange(status: Boolean, beneficiary: Beneficiary?) {
        val totalPercentage =
            uiState.totalPercentage.plus(beneficiary?.allocationPercentage?.toInt() ?: 0)
        val beneficiariesList = uiState.beneficiaryList.toMutableList()
        if (beneficiary != null && totalPercentage <= MAX_PERCENTAGE) {
            beneficiariesList.add(beneficiary)
            uiState = uiState.copy(
                addBeneficiaryState = status,
                beneficiaryList = beneficiariesList,
                totalPercentage = totalPercentage,
            )
            cleanUI()
        } else if (beneficiary == null) {
            uiState = uiState.copy(addBeneficiaryState = status)
        } else {
            uiState = uiState.copy(
                openDialog = DialogParameters(
                    titleResource = string.smart_account_beneficiary_max_percentage_dialog_title,
                    descriptionResource = string.smart_account_beneficiary_max_percentage_dialog_subtitle,
                    positiveResource = string.understood,
                    isActive = mutableStateOf(true)
                )
            )
        }
        validatePercentage()
    }

    private fun cleanUI() {
        uiState = uiState.copy(relationship = "", percentage = "", beneficiaryFullName = "")
    }

    private fun validateForm() {
        emitBaseEvent(
            OnFormValidateCompleted(
                isFormValid = uiState.beneficiaryFullName.isNotBlank() && uiState.percentage.isNotBlank() && uiState.relationship.isNotBlank()
            )
        )
    }

    private fun validatePercentage() {
        emitBaseEvent(OnFormValidateCompleted(isFormValid = uiState.addBeneficiaryState || uiState.totalPercentage == MAX_PERCENTAGE))
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is OnCallQueryRelationshipUseCase -> callQueryRelationshipUseCase(
                event.user,
                event.idBrand,
                event.option
            )
            is OnRelationshipValueChange -> onRelationShipValueChange(event.relationship)
            is OnBeneficiaryFullNameValueChange -> onFullNameValueChange(event.fullName)
            is OnPercentageValueChange -> onPercentageValueChange(event.percentage)
            is OnNextActionClick -> event.nextStepAction()
            is OnValidateForm -> validateForm()
            is OnAddBeneficiaryStateChange -> onAddBeneficiaryStateChange(
                event.status,
                event.beneficiary
            )
        }
    }

    data class UIState(
        val relationshipList: List<Relationship?> = listOf(),
        val beneficiaryList: List<Beneficiary> = listOf(),
        val beneficiaryFullName: String = "",
        val relationship: String = "",
        val percentage: String = "",
        val addBeneficiaryState: Boolean = true,
        val totalPercentage: Int = 0,
        val openDialog: DialogParameters = DialogParameters()
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
        data class OnAddBeneficiaryStateChange(
            val status: Boolean,
            val beneficiary: Beneficiary? = null
        ) : UIEvent()

        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val MAX_PERCENTAGE = 100
    }
}