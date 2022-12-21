package com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelOneUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelThreeUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartData
import com.multimoney.domain.model.accountsmart.Address
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnGetUserData
import com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress.SmartLivAddressViewModel.UIEvent.OnNotApplicable
import com.multimoney.multimoney.presentation.util.ADDRESS_MAX_LENGTH
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
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

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(accountSmartData: AccountSmartData?) {
        accountSmartData?.let {
            val divisionOne = it.strAddressLevel1
            val divisionTwo = it.strAddressLevel2
            val divisionThree = it.strAddressLevel3

            if (it.idBrand == Brand.CostaRica.id) {
                getDivisionOne(divisionOne, divisionTwo, divisionThree)
            } else {
                getDivisionTwo(divisionTwo, divisionThree)
            }
            uiState = uiState.copy(
                address = it.addressDetail ?: ""
            )
        }
    }

    private fun getDivisionOne(
        currentDivisionOne: String? = null,
        currentDivisionTwo: String? = null,
        currentDivisionThree: String? = null
    ) {
        executeUseCase {
            addressLevelOneUseCase(
                user = user,
                idBrand = idBrand
            ).collectLatest { result ->
                result.onSuccess { addressList ->
                    uiState = uiState.copy(
                        divisionOneList = addressList?.addresses,
                        isLoading = false
                    )

                    if (currentDivisionOne.isNullOrBlank().not()) {
                        uiState = uiState.copy(
                            divisionOneSelected = addressList?.addresses?.find { address ->
                                address?.name == currentDivisionOne
                            }
                        )
                        Log.d("AAAAAAA", "Division one: ${uiState.divisionOneSelected}")
                        getDivisionTwo(currentDivisionTwo, currentDivisionThree)
                    }
                }
                result.onLoading {
                    uiState = uiState.copy(
                        isLoading = true
                    )
                }
                result.onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            isActive = mutableStateOf(true),
                            titleResource = R.string.error,
                            description = error.getError() ?: ""
                        )
                    )
                }
            }
        }
    }

    private fun getDivisionTwo(
        currentDivisionTwo: String? = null,
        currentDivisionThree: String? = null
    ) {
        executeUseCase {
            uiState.divisionOneSelected?.code?.let {
                addressLevelTwoUseCase(
                    user,
                    idBrand,
                    it
                ).collectLatest { result ->
                    result.onSuccess { addressList ->
                        uiState = uiState.copy(
                            divisionTwoList = addressList?.addresses,
                            isLoading = false
                        )

                        if (currentDivisionTwo.isNullOrBlank().not()) {
                            uiState = uiState.copy(
                                divisionTwoSelected = addressList?.addresses?.find { address ->
                                    address?.name == currentDivisionTwo
                                }
                            )
                            Log.d("AAAAAAA", "Division one: ${uiState.divisionTwoSelected}")
                            getDivisionThree(currentDivisionThree)
                        }
                    }
                    result.onLoading {
                        uiState = uiState.copy(
                            isLoading = true
                        )
                    }
                    result.onFailure { error ->
                        uiState = uiState.copy(
                            isLoading = false,
                            openDialog = DialogParameters(
                                isActive = mutableStateOf(true),
                                titleResource = R.string.error,
                                description = error.getError() ?: ""
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getDivisionThree(currentDivisionThree: String? = null) {
        executeUseCase {
            uiState.divisionOneSelected?.code?.let { divOne ->
                uiState.divisionTwoSelected?.code?.let { divTwo ->
                    addressLevelThreeUseCase(
                        user = user,
                        idBrand = idBrand,
                        idAddressLevelOne = divOne,
                        idAddressLevelTwo = divTwo
                    ).collectLatest { result ->
                        result.onSuccess { addressList ->
                            uiState = uiState.copy(
                                divisionThreeList = addressList?.addresses,
                                isLoading = false
                            )

                            if (currentDivisionThree.isNullOrBlank().not()) {
                                uiState = uiState.copy(
                                    divisionThreeSelected = addressList?.addresses?.find { address ->
                                        address?.name == currentDivisionThree
                                    }
                                )
                                Log.d("AAAAAAA", "Division one: ${uiState.divisionThreeSelected}")
                                validate()
                            }
                        }
                        result.onLoading {
                            uiState = uiState.copy(isLoading = true)
                        }
                        result.onFailure { error ->
                            uiState = uiState.copy(
                                isLoading = false,
                                openDialog = DialogParameters(
                                    isActive = mutableStateOf(true),
                                    titleResource = R.string.error,
                                    description = error.getError() ?: ""
                                )
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
        uiState = uiState.copy(
            divisionThreeSelected = uiState.divisionThreeList?.find { it?.name == divisionThree }
        )
        validate()
    }

    private fun onAddressValueChange(address: String) {
        uiState = if (address.length <= ADDRESS_MAX_LENGTH) {
            uiState.copy(
                address = address,
                addressError = Pair(false, R.string.empty)
            )
        } else {
            uiState.copy(
                address = address,
                addressError = Pair(
                    true,
                    R.string.you_have_exceeded_the_max_characters_error
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
        uiState.address.isNotEmpty() &&
        uiState.addressError.first.not()

    sealed class UIEvent {
        data class OnGetUserData(
            val user: String,
            val idBrand: Int,
            val accountSmartData: AccountSmartData?
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

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnGetUserData -> {
                user = uiEvent.user
                idBrand = uiEvent.idBrand
                onLoadCurrentStepData(uiEvent.accountSmartData)
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

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    data class UIState(
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val divisionOneList: List<Address?>? = listOf(),
        val divisionTwoList: List<Address?>? = listOf(),
        val divisionThreeList: List<Address?>? = listOf(),
        val divisionOneSelected: Address? = null,
        val divisionTwoSelected: Address? = null,
        val divisionThreeSelected: Address? = null,
        val address: String = "",
        val addressError: Pair<Boolean, Int> = Pair(
            false,
            R.string.empty
        )
    )
}
