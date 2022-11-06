package com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelOneUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelThreeUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.domain.model.accountsmart.Address
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnGetUserData
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnNotApplicable
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartLivAddressViewModel @Inject constructor(
    val addressLevelOneUseCase: QueryAddressLevelOneUseCase,
    val addressLevelTwoUseCase: QueryAddressLevelTwoUseCase,
    val addressLevelThreeUseCase: QueryAddressLevelThreeUseCase
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    var user = ""
    var idBrand = 0

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnGetUserData -> {
                user = uiEvent.pkUser
                idBrand = uiEvent.idBrand
                setInitialQuery()
            }
            is OnDivisionOneValueChange -> {
                onDivisionOneValueChange(
                    uiEvent.divisionOne
                )
            }
            is OnDivisionTwoValueChange -> {
                onDivisionTwoValueChange(
                    uiEvent.divisionTwo
                )
            }
            is OnDivisionThreeValueChange -> {
                onDivisionThreeValueChange(
                    uiEvent.divisionThree
                )
            }
            is OnAddressValueChange -> {
                onAddressValueChange(uiEvent.address)
            }
            is OnNotApplicable -> {
                uiState = uiState.copy(
                    divisionOneSelected = Address(
                        code = uiEvent.value,
                        id = null,
                        name = null
                    )
                )
            }
        }
    }

    private fun setInitialQuery() {
        when (idBrand) {
            Brand.CostaRica.id -> {
                getDivisionOne(
                    idBrand,
                    user
                )
            }
            Brand.ElSalvador.id -> {
                getDivisionTwo()
            }
        }
    }

    private fun getDivisionOne(idBrand: Int, user: String) {
        executeUseCase {
            addressLevelOneUseCase(
                user = user,
                idBrand = idBrand
            ).collectLatest {
                it.onSuccess { addressList ->
                    uiState = uiState.copy(
                        divisionOneList = addressList?.addresses
                    )
                }
            }
        }
    }

    private fun getDivisionTwo() {
        executeUseCase {
            uiState.divisionOneSelected?.code?.let {
                addressLevelTwoUseCase(
                    user,
                    idBrand,
                    it
                ).collectLatest {
                    it.onSuccess { addressList ->
                        uiState = uiState.copy(
                            divisionTwoList = addressList?.addresses
                        )
                    }
                }
            }
        }
    }

    private fun getDivisionThree() {
        executeUseCase {
            uiState.divisionOneSelected?.code?.let { divOne ->
                uiState.divisionTwoSelected?.code?.let { divTwo ->
                    addressLevelThreeUseCase(
                        user = user,
                        idBrand = idBrand,
                        idAddressLevelOne = divOne,
                        idAddressLevelTwo = divTwo
                    ).collectLatest {
                        it.onSuccess { addressList ->
                            uiState = uiState.copy(
                                divisionThreeList = addressList?.addresses
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onDivisionOneValueChange(divisionOne: String?) {
        // reset selection of division two and three
        uiState = uiState.copy(
            divisionOneSelected = uiState.divisionOneList?.find { it?.name == divisionOne },
            divisionTwoSelected = null,
            divisionTwoList = listOf(),
            divisionThreeSelected = null,
            divisionThreeList = listOf()
        )
        getDivisionTwo()
    }

    private fun onDivisionTwoValueChange(divisionTwo: String?) {
        // reset selection of division three
        uiState = uiState.copy(
            divisionTwoSelected = uiState.divisionTwoList?.find { it?.name == divisionTwo },
            divisionThreeSelected = null,
            divisionThreeList = listOf()
        )
        getDivisionThree()
    }

    private fun onDivisionThreeValueChange(divisionThree: String?) {
        // reset selection of division two and three
        uiState = uiState.copy(
            divisionThreeSelected = uiState.divisionThreeList?.find { it?.name == divisionThree }
        )
        validate()
    }

    private fun onAddressValueChange(address: String) {
        uiState = if (address.length < ADDRESS_MAX_LENGHT) {
            uiState.copy(address = address)
        } else {
            uiState.copy(
                addressError = Pair(
                    true,
                    R.string.smart_own_business_description_max_char_error
                )
            )
        }
        validate()
    }

    private fun validate() {
        emitBaseEvent(
            BaseEvent.OnFormValidateCompleted(isFormValid())
        )
    }

    fun isFormValid() = uiState.divisionOneSelected != null &&
        uiState.divisionTwoSelected != null &&
        uiState.divisionThreeSelected != null &&
        uiState.address.isNotEmpty()

    sealed class UIEvent {
        data class OnGetUserData(
            val pkUser: String,
            val user: String,
            val idBrand: Int
        ) : UIEvent()

        data class OnDivisionOneValueChange(
            val divisionOne: String?,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()

        data class OnDivisionTwoValueChange(
            val divisionTwo: String?,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()

        data class OnDivisionThreeValueChange(
            val divisionThree: String?
        ) : UIEvent()

        data class OnAddressValueChange(
            val address: String
        ) : UIEvent()

        data class OnNotApplicable(
            val value: String
        ) : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    data class UIState(
        val isLoading: Boolean = false,
        val divisionOneList: List<Address?>? = listOf(),
        val divisionTwoList: List<Address?>? = listOf(),
        val divisionThreeList: List<Address?>? = listOf(),
        val divisionOneSelected: Address? = null,
        val divisionTwoSelected: Address? = null,
        val divisionThreeSelected: Address? = null,
        val address: String = "",
        val addressError: Pair<Boolean, Int> = Pair(
            false,
            R.string.credit_company_address_accurate_address_error
        )
    )

    companion object {
        const val ADDRESS_MAX_LENGHT = 150
    }
}
