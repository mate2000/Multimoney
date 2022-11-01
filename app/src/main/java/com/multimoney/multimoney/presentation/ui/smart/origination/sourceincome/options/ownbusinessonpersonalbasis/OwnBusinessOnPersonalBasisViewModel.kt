package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessonpersonalbasis

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.security.QueryCompanyNameByIdentityUseCase
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessonpersonalbasis.OwnBusinessOnPersonalBasisViewModel.UIEvent.OnBusinessActivityChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessonpersonalbasis.OwnBusinessOnPersonalBasisViewModel.UIEvent.OnIdentificationChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessonpersonalbasis.OwnBusinessOnPersonalBasisViewModel.UIEvent.OnIncomeAmountChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.options.ownbusinessonpersonalbasis.OwnBusinessOnPersonalBasisViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.util.DECIMAL_REGEX
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class OwnBusinessOnPersonalBasisViewModel @Inject constructor(
    private val queryCompany: QueryCompanyNameByIdentityUseCase
) : BaseViewModel(true) {
    var idBrand = Brand.ElSalvador.id

    var uiState by mutableStateOf(UIState())
        private set

    data class UIState(
        var businessIncome: String = "",
        var incomeError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        var businessActivity: String = "",
        var activityError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        var businessIdentification: String = "",
        var identificationSuccess: Pair<Boolean, Int> = Pair(false, R.string.empty),
        var identificationLoading: Pair<Boolean, Int> = Pair(false, R.string.empty),
        var identificationError: Pair<Boolean, String> = Pair(false, ""),
        var companyName: String = ""
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

    private fun businessActivityChange(source: String) {
        uiState = if (source.length < 150) {
            uiState.copy(businessActivity = source)
        } else {
            uiState.copy(
                activityError = Pair(
                    true,
                    R.string.smart_business_personal_basis_activity_error
                )
            )
        }
        onValidateForm()
    }

    private fun incomeAmountChange(income: String) {
        if (Pattern.matches(DECIMAL_REGEX, income) || income.isEmpty()) {
            uiState = uiState.copy(businessIncome = income)
        }
        onValidateForm()
    }

    private fun identificationChange(
        identification: String,
        idBrand: Int,
        user: String
    ) {
        if (identification.length < 11) {
            clearIdentificationStatus()
            uiState = uiState.copy(businessIdentification = identification)

            if (identification.length == 10) {
                callQueryGetCompanyUseCase(identification, idBrand, user)
            } else if (identification.length > 1) {
                uiState = uiState.copy(identificationError = Pair(true, "Revisar formato Debe tener 10 digitos"))
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
                        companyName = it?.name ?: "Company name not found"
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
                        identificationError = Pair(
                            true,
                            it.getError() ?: "Error confirming identification"
                        )
                    )
                }
            }
            onValidateForm()
        }
    }

    private fun clearIdentificationStatus() {
        uiState = uiState.copy(
            identificationError = Pair(false, ""),
            identificationLoading = Pair(false, R.string.empty),
            identificationSuccess = Pair(false, R.string.empty)
        )
    }

    private fun onValidateForm() {
        emitBaseEvent(BaseEvent.OnFormValidateCompleted(isFormValid()))
    }

    fun isFormValid(): Boolean = uiState.businessIncome.isNotBlank() &&
        uiState.businessActivity.isNotBlank() &&
        uiState.identificationSuccess.first &&
        uiState.businessIdentification.length == 10

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }
}
