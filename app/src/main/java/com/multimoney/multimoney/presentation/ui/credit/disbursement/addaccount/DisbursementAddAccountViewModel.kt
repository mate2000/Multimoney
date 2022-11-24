package com.multimoney.multimoney.presentation.ui.credit.disbursement.addaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.QueryBanksAndRegularExpressionUseCase
import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.credit.CreditCatalogOption
import com.multimoney.domain.model.credit.RegularExpression
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.EMAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.ui.credit.origination.util.SaveCreditStepsHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getRegex
import com.multimoney.multimoney.presentation.util.matchRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class DisbursementAddAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val saveCreditStepsHelper: SaveCreditStepsHelper,
    private val queryBanksAndRegularExpressionUseCase: QueryBanksAndRegularExpressionUseCase
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var bank: CreditCatalog? = null
    private var bankList: List<CreditCatalogOption?>? = listOf()
    var accountTypeList: List<RegularExpression?>? = listOf()
    var idBrand: Int? = null
    var pkUser: String = ""
    var identification: String = ""
    var email: String = ""
    var idUserRequest: Int?

    init {
        idBrand = savedStateHandle[ID_BRAND]
        pkUser = savedStateHandle[PK_USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        email = savedStateHandle[EMAIL] ?: ""
        idUserRequest = savedStateHandle[ID_USER_REQUEST]
        getTextResources()
    }

    private fun getTextResources() {
        uiState = uiState.copy(
            titleResource = when (idBrand) {
                Brand.ElSalvador.id -> R.string.disbursement_account_sv_title
                Brand.Guatemala.id -> R.string.disbursement_account_gt_title
                else -> R.string.disbursement_account_cr_title
            }
        )
    }

    private fun onLoad() {

        onCallQueryBanksAndRegularExpressions(
            pkUser = pkUser.toInt(),
            user = email,
            idBrand = idBrand ?: 0,
            idUserRequest = idUserRequest ?: 0,
            list = saveCreditStepsHelper.inputTextInfoList,
            onLoadingValueChange = { isLoading ->
                onUIEvent(UIEvent.OnLoadingValueChange(isLoading))
            },
            onFailureWithDialog = { isLoading, dialogParameter ->
                onUIEvent(
                    UIEvent.OnFailureWithDialog(
                        isLoading,
                        dialogParameter
                    )
                )
            }
        )
    }

    private fun onCallQueryBanksAndRegularExpressions(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int,
        list: List<CreditCatalog?>?,
        onLoadingValueChange: (isLoading: Boolean) -> Unit,
        onFailureWithDialog: (isLoading: Boolean, dialogParameter: DialogParameters) -> Unit
    ) = executeUseCase {
        queryBanksAndRegularExpressionUseCase.invoke(pkUser, user, idBrand, idUserRequest)
            .collectLatest { result ->
                result.onSuccess {
                    bank = it.banks?.first()
                    accountTypeList = it.regularExpression
                    bankList = bank?.subOptions?.filter { filter ->
                        filter?.description != MIDDLE_DASH
                    }
                    uiState = uiState.copy(bankList = bankList)
                    if (!bank?.pkCatalog.isNullOrEmpty()) {
                        loadStepsInfo(list)
                    }
                    onLoadingValueChange(false)
                }.onLoading {
                    onLoadingValueChange(true)
                }.onFailure {
                    onFailureWithDialog(
                        false,
                        DialogParameters(
                            description = it.getError().toString(),
                            isActive = mutableStateOf(true)
                        )
                    )
                }
            }
    }

    private fun onBankValueChanged(bankSelected: CreditCatalogOption?) {
        uiState = uiState.copy(
            bankSelected = bankSelected,
            accountTypeListFiltered = accountTypeList?.filter {
                it?.fkRegularExpression == bankSelected?.pkCatalog?.toInt()
            },
            accountTypeSelectedString = "",
            accountTypeSelected = null,
            accountNumber = "",
            accountNumberError = Pair(false, R.string.empty)
        )
        validateForm()
    }

    private fun onAccountTypeValueChange(regulaExpression: RegularExpression?) {
        uiState = uiState.copy(
            accountTypeSelectedString = regulaExpression?.description ?: "",
            accountTypeSelected = regulaExpression,
            accountNumber = "",
            accountNumberError = Pair(false, R.string.empty)
        )
        validateForm()
    }

    private fun onAccountNumberValueChanged(accountNumber: String) {
        uiState = uiState.copy(
            accountNumber = accountNumber,
            accountNumberError =
            if (matchRegex(
                    accountNumber,
                    getRegex(uiState.accountTypeSelected?.regularExpression.orEmpty())
                )
            ) {
                Pair(false, R.string.empty)
            } else {
                Pair(true, R.string.credit_bank_account_number_error)
            }
        )
        validateForm()
    }

    private fun validateForm() {
        uiState =
            uiState.copy(isContinueEnabled = uiState.bankSelected != null && uiState.accountTypeSelected != null && uiState.accountNumber.isNotEmpty())
    }

    data class UIState(
        val titleResource: Int = R.string.empty,
        val accountNumber: String = "",
        val accountNumberError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val bankList: List<CreditCatalogOption?>? = listOf(),
        val bankSelected: CreditCatalogOption? = null,
        val accountTypeListFiltered: List<RegularExpression?>? = listOf(),
        val accountTypeSelectedString: String = "",
        val accountTypeSelected: RegularExpression? = null,
        val isContinueEnabled: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnCallQueryBanksAndRegularExpression -> onLoad()
            is UIEvent.OnValidateForm -> validateForm()
            is UIEvent.OnAccountNumberValueChange -> onAccountNumberValueChanged(event.accountNumber)
            is UIEvent.OnBankValueChanged -> onBankValueChanged(event.bankSelected)
            is UIEvent.OnAccountTypeValueChanged -> onAccountTypeValueChange(event.regularExpressionSelected)
            is UIEvent.OnLoadingValueChange -> uiState = uiState.copy(isLoading = event.isLoading)
            is UIEvent.OnFailureWithDialog ->
                uiState =
                    uiState.copy(isLoading = event.isLoading, openDialog = event.openDialog)
            is UIEvent.OnBackClick -> navigateBack(false)
        }
    }

    private fun loadStepsInfo(list: List<CreditCatalog?>?) {
        val bankSelected = bankList?.find { it?.pkCatalog == bank?.pkCatalog }
        val accountType = list?.find { it?.description == SaveCreditStepsHelper.ACCOUNT_TYPE }
        val accountTypeListFiltered =
            accountTypeList?.filter { it?.fkRegularExpression == bankSelected?.pkCatalog?.toInt() }
        val accountNumber = list?.find { it?.description == SaveCreditStepsHelper.ACCOUNT_NUMBER }
        uiState = uiState.copy(
            bankSelected = bankSelected,
            accountTypeListFiltered = accountTypeListFiltered,
            accountTypeSelectedString = accountType?.value ?: "",
            accountTypeSelected = accountTypeListFiltered?.findLast { it?.description == accountType?.value },
            accountNumber = accountNumber?.value ?: ""
        )
        validateForm()
    }

    private fun navigateBack(isRestart: Boolean) =
        navigateBack(
            isRestart = isRestart,
            popTo = Screen.DisbursementAccountScreen.route
        )

    sealed class UIEvent {
        object OnCallQueryBanksAndRegularExpression : UIEvent()
        object OnValidateForm : UIEvent()
        data class OnAccountNumberValueChange(val accountNumber: String) : UIEvent()
        data class OnBankValueChanged(val bankSelected: CreditCatalogOption?) : UIEvent()
        data class OnAccountTypeValueChanged(val regularExpressionSelected: RegularExpression?) :
            UIEvent()

        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        data class OnFailureWithDialog(val isLoading: Boolean, val openDialog: DialogParameters) :
            UIEvent()

        data class OnBackClick(val focusManager: FocusManager) : UIEvent()
    }

    companion object {
        const val MIDDLE_DASH = "-"
    }

}