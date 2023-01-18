package com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.SmartSteps
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
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnAddressValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnDivisionOneValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnDivisionThreeValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnDivisionTwoValueChange
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnGetUserData
import com.multimoney.multimoney.presentation.ui.smart.origination.sourceincome.SourceIncomeViewModel.UIEvent.OnValidateForm
import com.multimoney.multimoney.presentation.util.ADDRESS_MAX_LENGTH
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType
import com.multimoney.multimoney.presentation.util.catalog.SourceIncomeOptionType.Retired
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SourceIncomeViewModel @Inject constructor(
    val addressLevelOneUseCase: QueryAddressLevelOneUseCase,
    val addressLevelTwoUseCase: QueryAddressLevelTwoUseCase,
    val addressLevelThreeUseCase: QueryAddressLevelThreeUseCase
) : BaseViewModel(false) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    /**
     * this function is intended to load the form data on the UI, after getting the
     * data coming from the current step (provided from the backend)
     */
    private fun onLoadCurrentStepData(
        user: String,
        idBrand: Int,
        accountSmartData: AccountSmartData?
    ) {
        accountSmartData?.let {
            if (accountSmartData.idEconomicActivity != Retired.id.toLong()) {
                val divisionOne = it.idJobLevel1
                val divisionTwo = it.idJobLevel2
                val divisionThree = it.idJobLevel3
                if (it.idBrand == Brand.CostaRica.id) {
                    getDivisionOne(user, idBrand, divisionOne, divisionTwo, divisionThree)
                } else {
                    uiState = uiState.copy(divisionOneSelected = Address("", "", ""))
                    getDivisionTwo(user, idBrand, divisionTwo, divisionThree)
                }
                uiState = uiState.copy(
                    address = it.fullJobAddress ?: ""
                )
            }
        }
    }

    /**
     * call this function on any internal screen from the step three in order to return to the
     * desired screen. In this case, the economical activity options one.
     */
    fun goBackToMainOptions() {
        onUIEvent(
            UIEvent.OnNavigateToSelectedSourceOfIncomeOption(
                SourceIncomeOptionType.MainSourceIncomeScreenType.id
            )
        )
    }

    /**
     * get the previous step based on the idBrand, since the total of screens in the
     * stepper is different across CR and SV, it should be obtained depending on the country.
     * Besides, this function will be shared across all the economical options screen.
     * @param idBrand to get the country id and handle the back step accordingly.
     */
    fun getPreviousStep(idBrand: Int): Int {
        return if (idBrand == Brand.ElSalvador.id) SmartSteps.Two.id else SmartSteps.One.id
    }

    /**
     * get the next step based on the idBrand, since the total of screens in the
     * stepper is different across CR and SV, it should be obtained depending on the country.
     * Besides, this function will be shared across all the economical options screen.
     * @param idBrand to get the country id and handle the next step accordingly.
     */
    fun getNextStep(idBrand: Int): Int {
        return if (idBrand == Brand.ElSalvador.id) SmartSteps.Four.id else SmartSteps.Three.id
    }

    private fun getDivisionOne(
        user: String,
        idBrand: Int,
        currentDivisionOne: Long? = null,
        currentDivisionTwo: Long? = null,
        currentDivisionThree: Long? = null
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

                    currentDivisionOne?.let {
                        uiState = uiState.copy(
                            divisionOneSelected = addressList?.addresses?.find { address ->
                                address?.id?.toLongOrNull() == it
                            }
                        )
                        getDivisionTwo(user, idBrand, currentDivisionTwo, currentDivisionThree)
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
        user: String,
        idBrand: Int,
        currentDivisionTwo: Long? = null,
        currentDivisionThree: Long? = null
    ) {
        executeUseCase {
            uiState.divisionOneSelected?.code?.let {
                addressLevelTwoUseCase(
                    user,
                    idBrand,
                    if (idBrand == Brand.CostaRica.id) it else NOT_APPLICABLE
                ).collectLatest { result ->
                    result.onSuccess { addressList ->
                        uiState = uiState.copy(
                            divisionTwoList = addressList?.addresses,
                            isLoading = false
                        )

                        currentDivisionTwo?.let {
                            uiState = uiState.copy(
                                divisionTwoSelected = addressList?.addresses?.find { address ->
                                    address?.id?.toLongOrNull() == it
                                }
                            )
                            getDivisionThree(user, idBrand, currentDivisionThree)
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

    private fun getDivisionThree(
        user: String,
        idBrand: Int,
        currentDivisionThree: Long? = null
    ) {
        executeUseCase {
            uiState.divisionOneSelected?.code?.let { divOne ->
                uiState.divisionTwoSelected?.code?.let { divTwo ->
                    addressLevelThreeUseCase(
                        user = user,
                        idBrand = idBrand,
                        idAddressLevelOne = if (idBrand == Brand.CostaRica.id) divOne else NOT_APPLICABLE,
                        idAddressLevelTwo = divTwo
                    ).collectLatest { result ->
                        result.onSuccess { addressList ->
                            uiState = uiState.copy(
                                divisionThreeList = addressList?.addresses,
                                isLoading = false
                            )
                            currentDivisionThree?.let {
                                uiState = uiState.copy(
                                    divisionThreeSelected = addressList?.addresses?.find { address ->
                                        address?.id?.toLongOrNull() == it
                                    }
                                )
                            }

                            onValidateForm()
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

    private fun onDivisionOneValueChange(
        user: String,
        idBrand: Int,
        divisionOne: String?
    ) {
        // reset selection of division two and three
        uiState = uiState.copy(
            divisionOneSelected = uiState.divisionOneList?.find { it?.name == divisionOne },
            divisionTwoSelected = null,
            divisionTwoList = listOf(),
            divisionThreeSelected = null,
            divisionThreeList = listOf()
        )
        getDivisionTwo(user, idBrand)
        onValidateForm()
    }

    private fun onDivisionTwoValueChange(
        user: String,
        idBrand: Int,
        divisionTwo: String?
    ) {
        // reset selection of division three
        uiState = uiState.copy(
            divisionTwoSelected = uiState.divisionTwoList?.find { it?.name == divisionTwo },
            divisionThreeSelected = null,
            divisionThreeList = listOf()
        )
        getDivisionThree(user, idBrand)
        onValidateForm()
    }

    private fun onDivisionThreeValueChange(divisionThree: String?) {
        uiState = uiState.copy(
            divisionThreeSelected = uiState.divisionThreeList?.find { it?.name == divisionThree }
        )
        onValidateForm()
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
        onValidateForm()
    }

    private fun onValidateForm() = emitBaseEvent(OnFormValidateCompleted(isFormValid()))

    fun isFormValid() = uiState.divisionOneSelected != null &&
        uiState.divisionTwoSelected != null &&
        uiState.divisionThreeSelected != null &&
        uiState.address.isBlank().not() &&
        uiState.addressError.first.not()

    data class UIState(
        // Interactions
        val selectedOption: Int = SourceIncomeOptionType.MainSourceIncomeScreenType.id,
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
        ),
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateToSelectedSourceOfIncomeOption -> {
                uiState = uiState.copy(selectedOption = uiEvent.selectedOption)
            }
            is OnDivisionOneValueChange -> {
                onDivisionOneValueChange(
                    uiEvent.user,
                    uiEvent.idBrand,
                    uiEvent.divisionOne
                )
            }
            is OnDivisionTwoValueChange -> {
                onDivisionTwoValueChange(
                    uiEvent.user,
                    uiEvent.idBrand,
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
            is OnGetUserData -> {
                onLoadCurrentStepData(uiEvent.user, uiEvent.idBrand, uiEvent.accountSmartData)
            }
            is OnValidateForm -> onValidateForm()
        }
    }

    sealed class UIEvent {
        data class OnNavigateToSelectedSourceOfIncomeOption(val selectedOption: Int) : UIEvent()
        data class OnDivisionOneValueChange(
            val user: String,
            val idBrand: Int,
            val divisionOne: String?
        ) : UIEvent()

        data class OnDivisionTwoValueChange(
            val user: String,
            val idBrand: Int,
            val divisionTwo: String?
        ) : UIEvent()

        data class OnDivisionThreeValueChange(
            val user: String,
            val idBrand: Int,
            val divisionThree: String?
        ) : UIEvent()

        data class OnAddressValueChange(
            val address: String
        ) : UIEvent()

        data class OnGetUserData(
            val user: String,
            val idBrand: Int,
            val accountSmartData: AccountSmartData?
        ) : UIEvent()

        object OnValidateForm : UIEvent()
    }

    sealed class BaseEvent {
        data class OnFormValidateCompleted(val isFormValid: Boolean) : BaseEvent()
    }

    companion object {
        const val NOT_APPLICABLE = "NA"
    }
}
