package com.multimoney.multimoney.presentation.ui.credit.companyaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryCompanyCantonUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyDistrictUseCase
import com.multimoney.domain.interaction.credit.QueryCompanyProvinceUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
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
import com.multimoney.multimoney.presentation.ui.credit.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

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
    var pkUser = ""
    var user = ""
    var idBrand = Brand.ElSalvador.id
    var idUserRequest: String = ""
    private var companyProvince: CreditCatalog? = null
    private var companyCanton: CreditCatalog? = null
    private var companyDistrict: CreditCatalog? = null
    private var companyProvinceList: List<CreditCatalogOption?>? = listOf()
    private var companyCantonList: List<CreditCatalogOption?>? = listOf()
    private var companyDistrictList: List<CreditCatalogOption?>? = listOf()

    private fun onDivisionOneValueChange(
        divisionOne: CreditCatalogOption?,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        uiState = uiState.copy(
            divisionOneSelected = divisionOne,
            divisionTwoList = listOf(),
            divisionTwoSelected = null,
            divisionThreeList = listOf(),
            divisionThreeSelected = null
        )
        divisionOne?.pkCatalog?.let {
            onCallQueryCompanyCanton(
                pkUser,
                user,
                idBrand,
                it,
                onLoadingValueChange,
                onFailureWithDialog
            )
        }
        onValidateScreen()
    }

    private fun onDivisionTwoValueChange(
        divisionTwo: CreditCatalogOption?,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        uiState =
            uiState.copy(divisionTwoSelected = divisionTwo, divisionThreeList = listOf(), divisionThreeSelected = null)
        if (idBrand != Brand.ElSalvador.id) {
            divisionTwo?.pkCatalog?.let {
                onCallQueryCompanyDistrict(
                    pkUser,
                    user,
                    idBrand,
                    it,
                    onLoadingValueChange,
                    onFailureWithDialog
                )
            }
        }
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

    private fun onValidateScreen() {
        emitBaseEvent(
            IsFormCompleted(
                when (idBrand) {
                    Brand.ElSalvador.id -> uiState.divisionOneSelected != null && uiState.divisionTwoSelected != null && uiState.address.isNotBlank()
                    else -> uiState.divisionOneSelected != null && uiState.divisionTwoSelected != null && uiState.divisionThreeSelected != null && uiState.address.isNotBlank()
                }
            )
        )
    }

    private fun onCallCatalogs(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: String,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        this.pkUser = pkUser
        this.user = user
        this.idBrand = idBrand
        this.idUserRequest = idUserRequest
        onCallQueryCompanyProvince(pkUser, user, idBrand, onLoadingValueChange, onFailureWithDialog)
    }

    private fun onCallQueryCompanyProvince(
        pkUser: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryCompanyProvinceUseCase.invoke(pkUser.toInt(), user, idBrand, idUserRequest)
            .collectLatest { result ->
                result.onSuccess {
                    companyProvince = it?.first()
                    companyProvinceList = companyProvince?.subOptions?.filter { filter ->
                        filter?.description != MIDDLE_DASH
                    }

                    uiState = uiState.copy(
                        divisionOneList = companyProvince?.subOptions?.filter { filter ->
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

    private fun onCallQueryCompanyCanton(
        pkUser: String,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryCompanyCantonUseCase.invoke(pkUser.toInt(), user, idBrand, fkCatalogIdentifier, idUserRequest)
            .collectLatest { result ->
                result.onSuccess {
                    companyCanton = it?.first()
                    companyCantonList = companyCanton?.subOptions?.filter { filter ->
                        filter?.description != MIDDLE_DASH
                    }
                    uiState = uiState.copy(
                        divisionTwoList = companyCanton?.subOptions?.filter { filter ->
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

    private fun onCallQueryCompanyDistrict(
        pkUser: String,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryCompanyDistrictUseCase.invoke(pkUser.toInt(), user, idBrand, fkCatalogIdentifier, idUserRequest)
            .collectLatest { result ->
                result.onSuccess {
                    companyDistrict = it?.first()
                    companyDistrictList = companyDistrict?.subOptions?.filter { filter ->
                        filter?.description != MIDDLE_DASH
                    }
                    uiState = uiState.copy(
                        divisionThreeList = companyDistrict?.subOptions?.filter { filter ->
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

    private fun onNextActionClick(
        user: String,
        nextStepAction: () -> Unit,
        saveCreditStepsHelper: SaveCreditStepsHelper
    ) {
        if (idBrand == Brand.ElSalvador.id) {
            saveCreditStepsHelper.saveStepThreeSV(
                user,
                companyProvince,
                uiState.divisionOneSelected,
                companyCanton,
                uiState.divisionTwoSelected,
                uiState.address
            )
        } else {
            saveCreditStepsHelper.saveStepThree(
                user,
                companyProvince,
                uiState.divisionOneSelected,
                companyCanton,
                uiState.divisionTwoSelected,
                companyDistrict,
                uiState.divisionThreeSelected,
                uiState.address
            )
        }
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
        val addressError: Pair<Boolean, Int> = Pair(false, R.string.credit_company_address_accurate_address_error)
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNextActionClick -> onNextActionClick(
                uiEvent.user,
                uiEvent.nextStepAction,
                uiEvent.saveCreditStepsHelper
            )
            is OnDivisionOneValueChange -> onDivisionOneValueChange(
                uiEvent.divisionOne,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog
            )
            is OnDivisionTwoValueChange -> onDivisionTwoValueChange(
                uiEvent.divisionTwo,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog
            )
            is OnDivisionThreeValueChange -> onDivisionThreeValueChange(uiEvent.divisionThree)
            is OnAddressValueChange -> onAddressValueChange(uiEvent.address)
            is OnCallCatalogs -> onCallCatalogs(
                uiEvent.pkUser,
                uiEvent.user,
                uiEvent.idBrand,
                uiEvent.idUserRequest,
                uiEvent.onLoadingValueChange,
                uiEvent.onFailureWithDialog
            )
            is OnFormValid -> onValidateScreen()
        }
    }

    sealed class UIEvent {
        data class OnNextActionClick(
            val user: String,
            val nextStepAction: () -> Unit,
            val saveCreditStepsHelper: SaveCreditStepsHelper
        ) : UIEvent()

        data class OnDivisionOneValueChange(
            val divisionOne: CreditCatalogOption?,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()

        data class OnDivisionTwoValueChange(
            val divisionTwo: CreditCatalogOption?,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()

        data class OnDivisionThreeValueChange(val divisionThree: CreditCatalogOption?) : UIEvent()
        data class OnAddressValueChange(val address: String) : UIEvent()
        data class OnCallCatalogs(
            val pkUser: String,
            val user: String,
            val idBrand: Int,
            val idUserRequest: String,
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