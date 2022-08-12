package com.multimoney.multimoney.presentation.ui.credit.companyaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.credit.QueryCompanyAddressSVUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyAddressUseCase
import com.multimoney.domain.model.credit.CatalogSubOptions
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.BaseEvent.IsFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnCallCatalogs
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnFormValid
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CompanyAddressViewModel @Inject constructor(
    private val queryCompanyAddressUseCase: QueryCompanyAddressUseCase,
    private val queryCompanyAddressSVUseCase: QueryCompanyAddressSVUseCase
) : BaseViewModel() {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    val country = ZERO
    private var companyCantonList: List<CatalogSubOptions?>? = listOf()
    private var companyDistrictList: List<CatalogSubOptions?>? = listOf()

    private fun onDivisionOneValueChange(divisionOne: CatalogSubOptions?) {
        uiState = uiState.copy(divisionOneSelected = divisionOne)
        uiState = uiState.copy(divisionTwoList = companyCantonList?.filter {
            it?.fkCatalog.toString() == uiState.divisionOneSelected?.pkCatalog
        })
        onValidateScreen()
    }

    private fun onDivisionTwoValueChange(divisionTwo: CatalogSubOptions?) {
        uiState = uiState.copy(divisionTwoSelected = divisionTwo)
        uiState = uiState.copy(divisionThreeList = companyDistrictList?.filter {
            it?.fkCatalog.toString() == uiState.divisionTwoSelected?.pkCatalog
        })
        onValidateScreen()
    }

    private fun onDivisionThreeValueChange(divisionThree: CatalogSubOptions?) {
        uiState = uiState.copy(divisionThreeSelected = divisionThree)
        onValidateScreen()
    }

    private fun onAddressValueChange(address: String) {
        uiState = uiState.copy(address = address)
        onValidateScreen()
    }

    private fun onValidateScreen() {
        emitBaseEvent(
            IsFormCompleted(
                when (country) {
                    TWO -> uiState.divisionOneSelected != null && uiState.divisionTwoSelected != null && uiState.address.isNotBlank()
                    else -> uiState.divisionOneSelected != null && uiState.divisionTwoSelected != null && uiState.divisionThreeSelected != null && uiState.address.isNotBlank()
                }
            )
        )
    }

    private fun onCallCatalogs(
        pkUser: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        if (country == TWO) {
            onCallQueryCompanyAddressSV(pkUser, user, idBrand, onLoadingValueChange, onFailureWithDialog)
        } else {
            onCallQueryCompanyAddress(pkUser, user, idBrand, onLoadingValueChange, onFailureWithDialog)
        }
    }

    private fun onCallQueryCompanyAddress(
        pkUser: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        executeUseCase {
            queryCompanyAddressUseCase.invoke(pkUser, user, idBrand)
                .collectLatest { result ->
                    result.onSuccess {
                        companyCantonList = it?.companyCanton?.first()?.subOptions?.filter { filter ->
                            filter?.description != MIDDLE_DASH
                        }
                        companyDistrictList = it?.companyDistrict?.first()?.subOptions?.filter { filter ->
                            filter?.description != MIDDLE_DASH
                        }
                        uiState = uiState.copy(
                            divisionOneList = it?.companyProvince?.first()?.subOptions?.filter { filter ->
                                filter?.description != MIDDLE_DASH
                            }
                        )
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
    }

    private fun onCallQueryCompanyAddressSV(
        pkUser: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        executeUseCase {
            queryCompanyAddressSVUseCase.invoke(pkUser, user, idBrand)
                .collectLatest { result ->
                    result.onSuccess {
                        companyCantonList = it?.companyCanton?.first()?.subOptions?.filter { filter ->
                            filter?.description != MIDDLE_DASH
                        }
                        uiState = uiState.copy(
                            divisionOneList = it?.companyProvince?.first()?.subOptions?.filter { filter ->
                                filter?.description != MIDDLE_DASH
                            }
                        )
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
    }

    private fun onNextActionClick(nextStepAction: () -> Unit) {
        nextStepAction()
    }

    data class UIState(
        val divisionOneList: List<CatalogSubOptions?>? = listOf(),
        val divisionTwoList: List<CatalogSubOptions?>? = listOf(),
        val divisionThreeList: List<CatalogSubOptions?>? = listOf(),
        val divisionOneSelected: CatalogSubOptions? = null,
        val divisionTwoSelected: CatalogSubOptions? = null,
        val divisionThreeSelected: CatalogSubOptions? = null,
        val address: String = "",
        val addressError: Pair<Boolean, Int> = Pair(false, R.string.credit_company_address_accurate_address_error)
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNextActionClick -> onNextActionClick(uiEvent.nextStepAction)
            is OnDivisionOneValueChange -> onDivisionOneValueChange(uiEvent.divisionOne)
            is OnDivisionTwoValueChange -> onDivisionTwoValueChange(uiEvent.divisionTwo)
            is OnDivisionThreeValueChange -> onDivisionThreeValueChange(uiEvent.divisionThree)
            is OnAddressValueChange -> onAddressValueChange(uiEvent.address)
            is OnCallCatalogs -> onCallCatalogs(
                uiEvent.pkUser,
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog
            )
            is OnFormValid -> onValidateScreen()
        }
    }

    sealed class UIEvent {
        data class OnNextActionClick(val nextStepAction: () -> Unit) : UIEvent()
        data class OnDivisionOneValueChange(val divisionOne: CatalogSubOptions?) : UIEvent()
        data class OnDivisionTwoValueChange(val divisionTwo: CatalogSubOptions?) : UIEvent()
        data class OnDivisionThreeValueChange(val divisionThree: CatalogSubOptions?) : UIEvent()
        data class OnAddressValueChange(val address: String) : UIEvent()
        data class OnCallCatalogs(
            val pkUser: String,
            val user: String,
            val idBrand: Int,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()

        object OnFormValid : UIEvent()
    }

    sealed class BaseEvent {
        data class IsFormCompleted(val isCompleted: Boolean) : BaseEvent()
    }

    companion object {
        const val ZERO = 0
        const val ONE = 1
        const val TWO = 2
        const val MIDDLE_DASH = "-"
    }
}