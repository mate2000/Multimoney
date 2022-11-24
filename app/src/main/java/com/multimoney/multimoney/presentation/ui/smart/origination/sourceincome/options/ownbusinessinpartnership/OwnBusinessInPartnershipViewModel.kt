package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.security.QueryCompanyNameByIdentityUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership.OwnBusinessInPartnershipViewModel.UIEvent.OnBusinessActivityChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership.OwnBusinessInPartnershipViewModel.UIEvent.OnIdentificationChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership.OwnBusinessInPartnershipViewModel.UIEvent.OnIncomeAmountChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessinpartnership.OwnBusinessInPartnershipViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.util.DESCRIPTION_MAX_LENGTH
import com.multimoney.multimoney.presentation.util.MIN_INCOME
import com.multimoney.multimoney.presentation.util.validateDecimalIncome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class OwnBusinessInPartnershipViewModel @Inject constructor(
    private val queryCompany: QueryCompanyNameByIdentityUseCase
) : BaseViewModel(true) {
    var uiState by mutableStateOf(UIState())
        private set

    private fun businessActivityChange(source: String) {
        uiState = if (source.length <= DESCRIPTION_MAX_LENGTH) {
            uiState.copy(
                businessActivity = source,
                businessActivityError = Pair(false, R.string.empty)
            )
        } else {
            uiState.copy(
                businessActivity = source,
                businessActivityError = Pair(
                    true,
                    R.string.smart_own_business_description_max_char_error
                )
            )
        }
        onValidateForm()
    }

    private fun incomeAmountChange(income: String) {
        if (validateDecimalIncome(income)) {
            uiState = uiState.copy(businessIncome = income)
        }
        onValidateForm()
    }

    private fun identificationChange(
        identification: String,
        idBrand: Int,
        user: String
    ) {
        if (identification.length <= IDENTIFICATION_LENGTH) {
            clearIdentificationStatus()
            uiState = uiState.copy(businessIdentification = identification)

            if (identification.length == IDENTIFICATION_LENGTH) {
                callQueryGetCompanyUseCase(identification, idBrand, user)
            } else if (identification.length > IDENTIFICATION_MIN_LENGTH) {
                uiState = uiState.copy(identificationError = Pair(true, R.string.smart_business_personal_basis_identification_format_error))
            }
        }
        onValidateForm()
    }

    private fun callQueryGetCompanyUseCase(
        identification: String,
        idBrand: Int,
        user: String
    ) {
        executeUseCase {
            queryCompany.invoke(
                identification = identification,
                idBrand = idBrand,
                user = user
            ).collectLatest { result ->
                result.onSuccess {
                    clearIdentificationStatus()
                    uiState = uiState.copy(
                        identificationSuccess = Pair(
                            true,
                            R.string.smart_business_personal_basis_identification_success
                        ),
                        companyName = it?.name
                    )
                }
                result.onLoading {
                    clearIdentificationStatus()
                    uiState = uiState.copy(
                        identificationLoading = Pair(
                            true,
                            R.string.smart_business_personal_basis_identification_loading
                        )
                    )
                }
                result.onFailure {
                    clearIdentificationStatus()
                    uiState = uiState.copy(
                        identificationError = Pair(true, R.string.empty),
                        identificationValidationError = it.getError()
                    )
                }
            }
            onValidateForm()
        }
    }

    private fun clearIdentificationStatus() {
        uiState = uiState.copy(
            identificationError = Pair(false, R.string.empty),
            identificationLoading = Pair(false, R.string.empty),
            identificationSuccess = Pair(false, R.string.empty),
            identificationValidationError = null,
            companyName = ""
        )
    }

    private fun onValidateForm() {
        emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))
    }

    fun isFormValid(): Boolean = uiState.businessIncome.isNotBlank() &&
        uiState.businessActivity.isNotBlank() &&
        uiState.identificationSuccess.first &&
        uiState.businessIdentification.length == IDENTIFICATION_LENGTH &&
        uiState.businessIncome.toFloat() > MIN_INCOME &&
        uiState.businessActivityError.first.not()

    data class UIState(
        var businessIncome: String = "",
        var businessActivity: String = "",
        var businessActivityError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        var businessIdentification: String = "",
        var identificationSuccess: Pair<Boolean, Int> = Pair(false, R.string.empty),
        var identificationLoading: Pair<Boolean, Int> = Pair(false, R.string.empty),
        var identificationError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        var identificationValidationError: String? = null,
        var companyName: String? = null
    )

    sealed class UIEvent {
        data class OnIncomeAmountChange(val income: String) : UIEvent()
        data class OnBusinessActivityChange(val activity: String) : UIEvent()
        data class OnIdentificationChange(
            val identification: String,
            val idBrand: Int,
            val user: String
        ) : UIEvent()
        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnIncomeAmountChange -> incomeAmountChange(uiEvent.income)
            is OnBusinessActivityChange -> businessActivityChange(uiEvent.activity)
            is OnIdentificationChange ->
                identificationChange(
                    uiEvent.identification,
                    uiEvent.idBrand,
                    uiEvent.user
                )
            is OnNextActionClick -> OnNextActionClick(uiEvent.nextStepAction)
        }
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val IDENTIFICATION_LENGTH = 10
        const val IDENTIFICATION_MIN_LENGTH = 1
    }
}
