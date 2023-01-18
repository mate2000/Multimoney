package com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QueryRelationshipUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.accountsmart.Relationship
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R.string
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnAddBeneficiaryOptionChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnAddBeneficiaryStateChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnBeneficiaryFullNameValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnCallQueryRelationshipUseCase
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnEditBeneficiaryClick
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnPercentageValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnRelationshipValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnRemoveBeneficiaryClick
import com.multimoney.multimoney.presentation.ui.smart.origination.beneficiaries.BeneficiariesViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.hasNumbersAndSpecialCharacters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class BeneficiariesViewModel @Inject constructor(
    private val queryRelationshipUseCase: QueryRelationshipUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())

    // Stateless
    private var beneficiaryToUpdate: Beneficiary? = null

    private fun callQueryRelationshipUseCase(user: String, idBrand: Int, idRequest: Int) =
        executeUseCase {
            queryRelationshipUseCase.invoke(user, idBrand, idRequest).collectLatest { result ->
                result.onSuccess { success ->
                    success?.data?.let {
                        uiState = uiState.copy(relationshipList = it)
                    }
                }
                result.onFailure {
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                }
            }
        }

    private fun onFullNameValueChange(name: String) {
        if (name.hasNumbersAndSpecialCharacters()) {
            uiState = uiState.copy(beneficiaryFullName = name)
        }
        validateForm()
    }

    private fun onRelationShipValueChange(relationShip: String) {
        uiState = uiState.copy(relationship = relationShip)
        validateForm()
    }

    private fun onPercentageValueChange(percentage: String) {
        if (percentage.isBlank()) {
            uiState = uiState.copy(percentage = percentage)
        } else {
            if (percentage.toInt() in MIN_PERCENTAGE..MAX_PERCENTAGE) {
                uiState = uiState.copy(percentage = percentage)
            }
        }
        validateForm()
    }

    private fun onAddBeneficiaryStateChange(status: Boolean, beneficiary: Beneficiary?) {
        // if the object is not null, then remove that beneficiary from the list before
        // adding a new beneficiary obtained from the form's data.
        beneficiaryToUpdate?.let {
            onRemoveBeneficiary(it)
            beneficiaryToUpdate = null
        }

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

    /**
     * fulfill the form' UI components with the previous beneficiary data
     * @param beneficiary the object that contains the data to be shown on the UI.
     */
    private fun onEditBeneficiary(beneficiary: Beneficiary) {
        uiState = uiState.copy(
            addBeneficiaryState = true,
            beneficiaryFullName = beneficiary.fullName.orEmpty(),
            relationship = beneficiary.strRelationship.orEmpty(),
            percentage = beneficiary.allocationPercentage.orEmpty()
        )
        beneficiaryToUpdate = beneficiary
    }

    private fun onShowAlertBeforeRemoveBeneficiary(beneficiary: Beneficiary) {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = string.smart_account_remove_beneficiary,
                descriptionResource = string.smart_account_remove_beneficiary_alert_description,
                positiveResource = string.common_remove,
                negativeResource = string.cancel,
                isActive = mutableStateOf(true),
                negativeAction = {
                    uiState = uiState.copy(showOptionsModal = true)
                },
                positiveAction = {
                    onRemoveBeneficiary(beneficiary)
                }
            )
        )
    }

    /**
     * remove the beneficiary from the local list, also update the total percentage
     * @param beneficiary the item that will be removed from the list
     */
    private fun onRemoveBeneficiary(beneficiary: Beneficiary) {
        val totalPercentage = uiState.totalPercentage.minus(
            beneficiary.allocationPercentage?.toInt() ?: 0
        )
        val beneficiaryList = uiState.beneficiaryList.toMutableList()
        beneficiaryList.remove(beneficiary)

        uiState = uiState.copy(
            beneficiaryList = beneficiaryList,
            totalPercentage = totalPercentage,
        )
    }

    private fun cleanUI() {
        uiState = uiState.copy(relationship = "", percentage = "", beneficiaryFullName = "")
    }

    private fun validateForm() {
        emitBaseEvent(
            OnFormValidateCompleted(
                isFormValid = uiState.beneficiaryFullName.isNotBlank() &&
                        uiState.percentage.isNotBlank() &&
                        uiState.relationship.isNotBlank()
            )
        )
    }

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(accountSmartData: AccountSmartData?) {
        accountSmartData?.let {
            uiState = uiState.copy(
                addBeneficiaryState = accountSmartData.listBeneficiaries?.isEmpty() == true,
                addBeneficiaryOption = accountSmartData.listBeneficiaries?.isNotEmpty() == true,
                beneficiaryList = accountSmartData.listBeneficiaries ?: emptyList(),
                totalPercentage = getTotalPercentage(accountSmartData.listBeneficiaries)
            )
            validatePercentage()
        }
    }

    private fun getTotalPercentage(beneficiaryList: List<Beneficiary>?) : Int {
        var percentage = 0
        if (beneficiaryList?.isEmpty() == true) {
            percentage = 0
        } else {
            beneficiaryList?.forEach {
                percentage += it.allocationPercentage?.toInt() ?: 0
            }
        }
        return percentage
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
            is UIEvent.OnValidatePercentage -> validatePercentage()
            is OnAddBeneficiaryStateChange -> onAddBeneficiaryStateChange(
                event.status,
                event.beneficiary
            )
            is OnEditBeneficiaryClick -> onEditBeneficiary(event.beneficiary)
            is OnRemoveBeneficiaryClick -> onShowAlertBeforeRemoveBeneficiary(event.beneficiary)
            is OnAddBeneficiaryOptionChange -> uiState = uiState.copy(addBeneficiaryOption = event.option)
            is UIEvent.OnLoadCurrentStepData -> onLoadCurrentStepData(event.accountSmartData)
        }
    }

    data class UIState(
        val relationshipList: List<Relationship?> = listOf(),
        val beneficiaryList: List<Beneficiary> = listOf(),
        val beneficiaryFullName: String = "",
        val relationship: String = "",
        val percentage: String = "",
        val addBeneficiaryState: Boolean = false,
        val totalPercentage: Int = 0,
        var showOptionsModal: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val addBeneficiaryOption: Boolean = false
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
        data class OnEditBeneficiaryClick(val beneficiary: Beneficiary) : UIEvent()
        data class OnRemoveBeneficiaryClick(val beneficiary: Beneficiary) : UIEvent()
        data class OnAddBeneficiaryStateChange(
            val status: Boolean,
            val beneficiary: Beneficiary? = null
        ) : UIEvent()

        data class OnAddBeneficiaryOptionChange(val option: Boolean) : UIEvent()
        data class OnLoadCurrentStepData(val accountSmartData: AccountSmartData?) : UIEvent()
        object OnValidateForm : UIEvent()
        object OnValidatePercentage : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val MAX_PERCENTAGE = 100
        const val MIN_PERCENTAGE = 1
    }
}
