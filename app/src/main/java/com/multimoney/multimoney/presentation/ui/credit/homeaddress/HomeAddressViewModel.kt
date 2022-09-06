package com.multimoney.multimoney.presentation.ui.credit.homeaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryHomeAddressSVUseCase
import com.multimoney.domain.interaction.credit.QueryHomeAddressUseCase
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.BaseEvent.IsFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnCallCatalogs
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnFormValid
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class HomeAddressViewModel @Inject constructor(
    private val queryHomeAddressUseCase: QueryHomeAddressUseCase,
    private val queryHomeAddressSVUseCase: QueryHomeAddressSVUseCase
) : BaseViewModel() {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    var idBrand = Brand.ElSalvador.id
    private var homeCantonList: List<CreditCatalogOption?>? = listOf()
    private var homeDistrictList: List<CreditCatalogOption?>? = listOf()

    private fun onDivisionOneValueChange(divisionOne: CreditCatalogOption?) {
        uiState = uiState.copy(divisionOneSelected = divisionOne)
        onValidateScreen()
    }

    private fun onDivisionTwoValueChange(divisionTwo: CreditCatalogOption?) {
        uiState = uiState.copy(divisionTwoSelected = divisionTwo)
        onValidateScreen()
    }

    private fun onDivisionThreeValueChange(divisionThree: CreditCatalogOption?) {
        uiState = uiState.copy(divisionThreeSelected = divisionThree)
        onValidateScreen()
    }

    private fun onAddressValueChange(address: String) {
        uiState = uiState.copy(address = address)
        onValidateScreen()
    }

    private fun onPhoneValueChange(phone: String) {
        uiState = uiState.copy(phone = phone)
        onValidateScreen()
    }

    private fun onValidateScreen() {
        emitBaseEvent(
            IsFormCompleted(
                when (idBrand) {
                    Brand.ElSalvador.id -> uiState.divisionOneSelected != null && uiState.divisionTwoSelected != null && uiState.address.isNotBlank() && uiState.phone.isNotBlank()
                    Brand.Guatemala.id -> uiState.divisionOneSelected != null && uiState.divisionTwoSelected != null && uiState.divisionThreeSelected != null && uiState.address.isNotBlank() && uiState.phone.isNotBlank()
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
        this.idBrand = idBrand
        if (idBrand == Brand.ElSalvador.id) {
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
            queryHomeAddressUseCase.invoke(pkUser, user, idBrand)
                .collectLatest { result ->
                    result.onSuccess {
                        homeCantonList = it?.canton?.first()?.subOptions?.filter { filter ->
                            filter?.description != MIDDLE_DASH
                        }
                        homeDistrictList = it?.district?.first()?.subOptions?.filter { filter ->
                            filter?.description != MIDDLE_DASH
                        }
                        uiState = uiState.copy(
                            divisionOneList = it?.province?.first()?.subOptions?.filter { filter ->
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
            queryHomeAddressSVUseCase.invoke(pkUser, user, idBrand)
                .collectLatest { result ->
                    result.onSuccess {
                        homeCantonList = it?.canton?.first()?.subOptions?.filter { filter ->
                            filter?.description != MIDDLE_DASH
                        }
                        uiState = uiState.copy(
                            divisionOneList = it?.province?.first()?.subOptions?.filter { filter ->
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
        val divisionOneList: List<CreditCatalogOption?>? = listOf(),
        val divisionTwoList: List<CreditCatalogOption?>? = listOf(),
        val divisionThreeList: List<CreditCatalogOption?>? = listOf(),
        val divisionOneSelected: CreditCatalogOption? = null,
        val divisionTwoSelected: CreditCatalogOption? = null,
        val divisionThreeSelected: CreditCatalogOption? = null,
        val address: String = "",
        val addressError: Pair<Boolean, Int> = Pair(false, R.string.credit_company_address_accurate_address_error),
        val phone: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNextActionClick -> onNextActionClick(uiEvent.nextStepAction)
            is OnDivisionOneValueChange -> onDivisionOneValueChange(uiEvent.divisionOne)
            is OnDivisionTwoValueChange -> onDivisionTwoValueChange(uiEvent.divisionTwo)
            is OnDivisionThreeValueChange -> onDivisionThreeValueChange(uiEvent.divisionThree)
            is OnAddressValueChange -> onAddressValueChange(uiEvent.address)
            is OnPhoneNumberValueChange -> onPhoneValueChange(uiEvent.phone)
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
        data class OnDivisionOneValueChange(val divisionOne: CreditCatalogOption?) : UIEvent()
        data class OnDivisionTwoValueChange(val divisionTwo: CreditCatalogOption?) : UIEvent()
        data class OnDivisionThreeValueChange(val divisionThree: CreditCatalogOption?) : UIEvent()
        data class OnAddressValueChange(val address: String) : UIEvent()
        data class OnPhoneNumberValueChange(val phone: String) : UIEvent()
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
        const val MIDDLE_DASH = "-"
    }
}