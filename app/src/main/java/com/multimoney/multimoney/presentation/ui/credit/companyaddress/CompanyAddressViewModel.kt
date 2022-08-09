package com.multimoney.multimoney.presentation.ui.credit.companyaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.credit.QueryCompanyProvinceUseCase
import com.multimoney.domain.model.credit.CatalogSubOptions
import com.multimoney.domain.model.credit.CompanyProvince
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnCallCompanyProvince
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CompanyAddressViewModel @Inject constructor(
    private val queryCompanyProvinceUseCase: QueryCompanyProvinceUseCase
) : BaseViewModel() {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    val country = ZERO

    //Event
    val companyProvinceEvent = MutableSharedFlow<MultimoneyResult<List<CompanyProvince?>>>()

    private fun onDivisionOneValueChange(divisionOne: CatalogSubOptions?) {
        uiState = uiState.copy(divisionOneSelected = divisionOne)
    }

    private fun onDivisionTwoValueChange(divisionTwo: CatalogSubOptions?) {
        uiState = uiState.copy(divisionTwoSelected = divisionTwo)
    }

    private fun onDivisionThreeValueChange(divisionThree: CatalogSubOptions?) {
        uiState = uiState.copy(divisionThreeSelected = divisionThree)
    }

    private fun onAddressValueChange(address: String) {
        uiState = uiState.copy(address = address)
    }

    private fun onCallQueryCompanyProvince(
        pkUser: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        executeUseCase {
            queryCompanyProvinceUseCase.invoke(pkUser, user, idBrand).collectLatest { result ->
                result.onSuccess {
                    uiState = uiState.copy(divisionOneList = it?.first()?.subOptions)
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
            is OnCallCompanyProvince -> onCallQueryCompanyProvince(
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
        data class OnCallCompanyProvince(
            val pkUser: String,
            val user: String,
            val idBrand: Int,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()
    }

    companion object {
        const val ZERO = 0
        const val ONE = 1
        const val TWO = 2
    }
}