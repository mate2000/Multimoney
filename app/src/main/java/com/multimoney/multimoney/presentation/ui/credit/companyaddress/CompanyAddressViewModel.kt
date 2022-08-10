package com.multimoney.multimoney.presentation.ui.credit.companyaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryCompanyCantonUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyDistrictUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyProvinceUseCase
import com.multimoney.domain.model.credit.CatalogSubOptions
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.BaseEvent.IsFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnCallInitialCatalog
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyAddressViewModel @Inject constructor(
    private val queryCompanyProvinceUseCase: QueryCompanyProvinceUseCase,
    private val queryCompanyCantonUseCase: QueryCompanyCantonUseCase,
    private val queryCompanyDistrictUseCase: QueryCompanyDistrictUseCase
) : BaseViewModel() {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    val country = ZERO
    private var companyCantonList: List<CatalogSubOptions?>? = listOf()
    private var companyDistrictList: List<CatalogSubOptions?>? = listOf()
    private var pkUser: String = ""
    private var user: String = ""
    private var idBrand: Int = Brand.Revamp.id
    private var onLoadingValueChange: (isLoading: Boolean) -> Unit = {}
    private var onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit = { _, _ -> }

    private fun onDivisionOneValueChange(divisionOne: CatalogSubOptions?) {
        uiState = uiState.copy(divisionOneSelected = divisionOne)
        if (companyCantonList.isNullOrEmpty()) {
            onCallQueryCompanyCanton(pkUser, user, idBrand, onLoadingValueChange, onFailureWithDialog)
        } else {
            uiState = uiState.copy(divisionTwoList = companyCantonList?.filter {
                it?.fkCatalog.toString() == uiState.divisionOneSelected?.pkCatalog
            })
        }
        validateScreen()
    }

    private fun onDivisionTwoValueChange(divisionTwo: CatalogSubOptions?) {
        uiState = uiState.copy(divisionTwoSelected = divisionTwo)
        if (companyDistrictList.isNullOrEmpty()) {
            onCallQueryCompanyDistrict(pkUser, user, idBrand, onLoadingValueChange, onFailureWithDialog)
        } else {
            uiState = uiState.copy(divisionThreeList = companyDistrictList?.filter {
                it?.fkCatalog.toString() == uiState.divisionTwoSelected?.pkCatalog
            })
        }
        validateScreen()
    }

    private fun onDivisionThreeValueChange(divisionThree: CatalogSubOptions?) {
        uiState = uiState.copy(divisionThreeSelected = divisionThree)
        validateScreen()
    }

    private fun onAddressValueChange(address: String) {
        uiState = uiState.copy(address = address)
        validateScreen()
    }

    private fun validateScreen() {
        emitBaseEvent(
            IsFormCompleted(
                when (country) {
                    TWO -> uiState.divisionOneSelected != null && uiState.divisionTwoSelected != null && uiState.address.isNotBlank()
                    else -> uiState.divisionOneSelected != null && uiState.divisionTwoSelected != null && uiState.divisionThreeSelected != null && uiState.address.isNotBlank()
                }
            )
        )
    }

    private fun initialCall(
        pkUser: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        this.pkUser = pkUser
        this.user = user
        this.idBrand
        this.onLoadingValueChange = onLoadingValueChange
        this.onFailureWithDialog = onFailureWithDialog
        onCallQueryCompanyProvince(pkUser, user, idBrand, onLoadingValueChange, onFailureWithDialog)
    }

    private fun onCallQueryCompanyProvince(
        pkUser: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        executeUseCase {
            queryCompanyProvinceUseCase.invoke(pkUser, user, idBrand)
                .collectLatest { result ->
                    result.onSuccess {
                        uiState = uiState.copy(divisionOneList = it?.first()?.subOptions?.filter { filter ->
                            filter?.description != MIDDLE_DASH
                        })
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

    private fun onCallQueryCompanyCanton(
        pkUser: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit,
    ) {
        viewModelScope.launch {
            queryCompanyCantonUseCase.invoke(pkUser, user, idBrand)
                .collectLatest { result ->
                    result.onSuccess { cantonList ->
                        companyCantonList = cantonList?.first()?.subOptions
                        uiState = uiState.copy(divisionTwoList = companyCantonList?.filter {
                            it?.fkCatalog.toString() == uiState.divisionOneSelected?.pkCatalog
                        })
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

    private fun onCallQueryCompanyDistrict(
        pkUser: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit,
    ) {
        viewModelScope.launch {
            queryCompanyDistrictUseCase.invoke(pkUser, user, idBrand)
                .collectLatest { result ->
                    result.onSuccess { districtList ->
                        companyDistrictList = districtList?.first()?.subOptions
                        uiState = uiState.copy(divisionThreeList = companyDistrictList?.filter {
                            it?.fkCatalog.toString() == uiState.divisionTwoSelected?.pkCatalog
                        })
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

    fun onUiEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnDivisionOneValueChange -> onDivisionOneValueChange(uiEvent.divisionOne)
            is OnDivisionTwoValueChange -> onDivisionTwoValueChange(uiEvent.divisionTwo)
            is OnDivisionThreeValueChange -> onDivisionThreeValueChange(uiEvent.divisionThree)
            is OnAddressValueChange -> onAddressValueChange(uiEvent.address)
            is OnCallInitialCatalog -> initialCall(
                uiEvent.pkUser,
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog
            )
        }
    }

    sealed class UIEvent {
        data class OnDivisionOneValueChange(val divisionOne: CatalogSubOptions?) : UIEvent()
        data class OnDivisionTwoValueChange(val divisionTwo: CatalogSubOptions?) : UIEvent()
        data class OnDivisionThreeValueChange(val divisionThree: CatalogSubOptions?) : UIEvent()
        data class OnAddressValueChange(val address: String) : UIEvent()
        data class OnCallInitialCatalog(
            val pkUser: String,
            val user: String,
            val idBrand: Int,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()
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