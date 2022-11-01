package com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelOneUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelThreeUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.domain.model.accountsmart.Address
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnGetUserData
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.util.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class SmartLivAddressViewModel @Inject constructor(
    val addressLevelOneUseCase: QueryAddressLevelOneUseCase,
    val addressLevelTwoUseCase: QueryAddressLevelTwoUseCase,
    val addressLevelThreeUseCase: QueryAddressLevelThreeUseCase
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // ViewModel vars
    var _user = ""
    var _idBrand = 0

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnGetUserData -> {
                _user = uiEvent.pkUser
                _idBrand = uiEvent.idBrand
                getDivisionOne(
                    _idBrand,
                    _user
                )
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

    fun onDivisionOneValueChange(divisionOne: Address?) {
        // reset selection of division two and three
        uiState = uiState.copy(
            divisionOneSelected = divisionOne,
            divisionTwoSelected = null,
            divisionTwoList = listOf(),
            divisionThreeSelected = null,
            divisionThreeList = listOf()
        )
        getDivisionTwo()
    }

    fun onDivisionTwoValueChange(divisionTwo: Address?) {
        // reset selection of division two and three
        uiState = uiState.copy(
            divisionTwoSelected = divisionTwo,
            divisionThreeSelected = null,
            divisionThreeList = listOf()
        )
        getDivisionThree()
    }

    fun onDivisionThreeValueChange(divisionThree: Address?) {
        // reset selection of division two and three
        uiState = uiState.copy(
            divisionThreeSelected = divisionThree,
            divisionThreeList = listOf()
        )
        validate()
    }

    private fun onAddressValueChange(address: String) {
        uiState = uiState.copy(address = address)
        validate()
    }

    private fun validate(){

    }

    private fun getDivisionTwo() {
        executeUseCase {
            uiState.divisionOneSelected?.name?.let {
                addressLevelTwoUseCase(
                    _user,
                    _idBrand,
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
            uiState.divisionOneSelected?.name?.let { divOne ->
                uiState.divisionTwoSelected?.name?.let { divTwo ->
                    addressLevelThreeUseCase(
                        _user,
                        _idBrand,
                        divOne,
                        divTwo
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

    sealed class UIEvent {
        data class OnGetUserData(
            val pkUser: String,
            val user: String,
            val idBrand: Int
        ) : UIEvent()
        data class OnDivisionOneValueChange(
            val divisionOne: Address?,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()
        data class OnDivisionTwoValueChange(
            val divisionTwo: Address?,
            val onLoadingValueChange: (status: Boolean) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()
        data class OnDivisionThreeValueChange(
            val divisionThree: Address?,
        ) : UIEvent()
        data class OnAddressValueChange(
            val address: String,
        ) : UIEvent()
    }


    data class UIState(
        val divisionOneList: List<Address?>? = listOf(),
        val divisionTwoList: List<Address?>? = listOf(),
        val divisionThreeList: List<Address?>? = listOf(),
        val divisionOneSelected: Address? = null,
        val divisionTwoSelected: Address? = null,
        val divisionThreeSelected: Address? = null,
        val address: String = "",
        val addressError: Pair<Boolean, Int> = Pair(false, R.string.credit_company_address_accurate_address_error)
        )
}
