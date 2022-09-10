package com.multimoney.multimoney.presentation.ui.credit.homeaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryHomeCantonUseCase
import com.multimoney.domain.interaction.credit.QueryHomeDistrictUseCase
import com.multimoney.domain.interaction.credit.QueryHomeProvinceUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.credit.companyaddress.CompanyAddressViewModel
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.BaseEvent.IsFormCompleted
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnCallCatalogs
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnFormValid
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnNextActionClick
import com.multimoney.multimoney.presentation.ui.credit.homeaddress.HomeAddressViewModel.UIEvent.OnPhoneNumberValueChange
import com.multimoney.multimoney.presentation.ui.credit.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class HomeAddressViewModel @Inject constructor(
    private val queryHomeProvinceUseCase: QueryHomeProvinceUseCase,
    private val queryHomeCantonUseCase: QueryHomeCantonUseCase,
    private val queryHomeDistrictUseCase: QueryHomeDistrictUseCase
) : BaseViewModel() {
    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    var pkUser = ""
    var user = ""
    var idBrand = Brand.ElSalvador.id
    private var homeProvince: CreditCatalog? = null
    private var homeCanton: CreditCatalog? = null
    private var homeDistrict: CreditCatalog? = null
    private var homeProvinceList: List<CreditCatalogOption?>? = listOf()
    private var homeCantonList: List<CreditCatalogOption?>? = listOf()
    private var homeDistrictList: List<CreditCatalogOption?>? = listOf()

    private fun onDivisionOneValueChange(
        divisionOne: CreditCatalogOption?,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) {
        uiState = uiState.copy(
            divisionOneSelected = divisionOne,
            divisionTwoSelected = null,
            divisionTwoList = listOf(),
            divisionThreeSelected = null,
            divisionThreeList = listOf()
        )
        divisionOne?.fkCatalog?.let {
            onCallQueryHomeCanton(
                pkUser,
                user,
                idBrand,
                it.toString(),
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
            uiState.copy(divisionTwoSelected = divisionTwo, divisionThreeSelected = null, divisionThreeList = listOf())
        divisionTwo?.fkCatalog?.let {
            onCallQueryHomeDistrict(
                pkUser,
                user,
                idBrand,
                it.toString(),
                onLoadingValueChange,
                onFailureWithDialog
            )
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
        this.pkUser = pkUser
        this.user = user
        this.idBrand = idBrand
        onCallQueryHomeProvince(pkUser, user, idBrand, onLoadingValueChange, onFailureWithDialog)
    }

    private fun onCallQueryHomeProvince(
        pkUser: String,
        user: String,
        idBrand: Int,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryHomeProvinceUseCase.invoke(pkUser.toInt(), user, idBrand)
            .collectLatest { result ->
                result.onSuccess {
                    homeProvince = it?.first()
                    homeProvinceList = homeProvince?.subOptions?.filter { filter ->
                        filter?.description != CompanyAddressViewModel.MIDDLE_DASH
                    }
                    uiState = uiState.copy(
                        divisionOneList = homeProvince?.subOptions?.filter { filter ->
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

    private fun onCallQueryHomeCanton(
        pkUser: String,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryHomeCantonUseCase.invoke(pkUser.toInt(), user, idBrand, fkCatalogIdentifier)
            .collectLatest { result ->
                result.onSuccess {
                    homeCanton = it?.first()
                    homeCantonList = homeCanton?.subOptions?.filter { filter ->
                        filter?.description != CompanyAddressViewModel.MIDDLE_DASH
                    }
                    uiState = uiState.copy(
                        divisionTwoList = homeCanton?.subOptions?.filter { filter ->
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

    private fun onCallQueryHomeDistrict(
        pkUser: String,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        onLoadingValueChange: (status: Boolean) -> Unit,
        onFailureWithDialog: (status: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryHomeDistrictUseCase.invoke(pkUser.toInt(), user, idBrand, fkCatalogIdentifier)
            .collectLatest { result ->
                result.onSuccess {
                    homeDistrict = it?.first()
                    homeDistrictList = homeDistrict?.subOptions?.filter { filter ->
                        filter?.description != CompanyAddressViewModel.MIDDLE_DASH
                    }
                    uiState = uiState.copy(
                        divisionThreeList = homeDistrict?.subOptions?.filter { filter ->
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
        when (idBrand) {
            Brand.ElSalvador.id -> {
                saveCreditStepsHelper.saveStepFourSV(
                    user,
                    homeProvince,
                    uiState.divisionOneSelected,
                    homeCanton,
                    uiState.divisionTwoSelected,
                    uiState.address,
                    uiState.phone
                )
            }
            Brand.Guatemala.id -> {
                saveCreditStepsHelper.saveStepFourGT(
                    user,
                    homeProvince,
                    uiState.divisionOneSelected,
                    homeCanton,
                    uiState.divisionTwoSelected,
                    homeDistrict,
                    uiState.divisionThreeSelected,
                    uiState.address,
                    uiState.phone
                )
            }
            Brand.CostaRica.id -> {
                saveCreditStepsHelper.saveStepThree(
                    user,
                    homeProvince,
                    uiState.divisionOneSelected,
                    homeCanton,
                    uiState.divisionTwoSelected,
                    homeDistrict,
                    uiState.divisionThreeSelected,
                    uiState.address
                )
            }
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
        val addressError: Pair<Boolean, Int> = Pair(false, R.string.credit_company_address_accurate_address_error),
        val phone: String = ""
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNextActionClick -> onNextActionClick(uiEvent.user, uiEvent.nextStepAction, uiEvent.saveCreditStepsHelper)
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