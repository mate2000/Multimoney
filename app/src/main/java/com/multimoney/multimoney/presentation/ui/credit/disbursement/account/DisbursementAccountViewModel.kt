package com.multimoney.multimoney.presentation.ui.credit.disbursement.account

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.credit.MutationProcessCreditExtensionDetailUseCase
import com.multimoney.domain.interaction.credit.QueryGetClientBankAccountUseCase
import com.multimoney.domain.interaction.credit.QueryGetExchangeRateCreditUseCase
import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.error.MessageError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onMessage
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.CREDIT_NUMBER
import com.multimoney.multimoney.presentation.navigation.navgraph.CURRENCY_ID
import com.multimoney.multimoney.presentation.navigation.navgraph.FK_FLOW_CONTROL
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_LOAN_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_USER_REQUEST
import com.multimoney.multimoney.presentation.navigation.navgraph.NEXT_PAYMENT_DATE
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.QUOTA_TOTAL
import com.multimoney.multimoney.presentation.navigation.navgraph.SELECTED_AMOUNT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnCallQueryGetClientBankAccount
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnCallQueryGetExchangeRateCredit
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnClientBankAccountSelected
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnHideDisbursementBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnLoadingValueChange
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnMessageProcessCreditExtension
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnNavigateBackHome
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnNavigateToDisbursementAddAccount
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnProcessCreditExtension
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnShowDisbursementBottomSheet
import com.multimoney.multimoney.presentation.ui.credit.disbursement.account.DisbursementAccountViewModel.UIEvent.OnSuccessProcessCreditExtension
import com.multimoney.multimoney.presentation.ui.credit.origination.amount.CreditAmountViewModel
import com.multimoney.multimoney.presentation.util.API_DATE_FORMAT
import com.multimoney.multimoney.presentation.util.BAR_DIVIDER_FORMAT_YEAR_TWO_DIGITS
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.formattedTwoDecimalsNumber
import com.multimoney.multimoney.presentation.util.getCardDateFormat
import com.multimoney.multimoney.presentation.util.getCurrency
import com.multimoney.multimoney.presentation.util.stringToDoubleFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalMaterialApi::class)
class DisbursementAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryGetClientBankAccountUseCase: QueryGetClientBankAccountUseCase,
    private val queryGetExchangeRateCreditUseCase: QueryGetExchangeRateCreditUseCase,
    private val mutationProcessCreditExtensionDetailUseCase: MutationProcessCreditExtensionDetailUseCase,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    // uiState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var idClient: Int = 0
    private var idLoanClient: Long = 0L
    private var identification: String? = null
    private var nextPaymentDate: String? = null
    private var quotaTotal: String? = null
    private var selectedAmount: String? = null
    private var pkUser: Int? = null
    private var currencyId: Int? = null
    private var idUserRequest: Int? = null
    private var creditNumber: String? = null
    private var fkFlowControl: Int? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        idClient = savedStateHandle[ID_CLIENT] ?: 0
        idLoanClient = savedStateHandle[ID_LOAN_CLIENT] ?: 0L
        nextPaymentDate = savedStateHandle[NEXT_PAYMENT_DATE]
        quotaTotal = savedStateHandle[QUOTA_TOTAL]
        selectedAmount = savedStateHandle[SELECTED_AMOUNT]
        pkUser = savedStateHandle.get<String>(PK_USER)?.toInt()
        idUserRequest = savedStateHandle[ID_USER_REQUEST]
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        creditNumber = savedStateHandle[CREDIT_NUMBER]
        fkFlowControl = savedStateHandle[FK_FLOW_CONTROL] ?: 0
        currencyId = savedStateHandle[CURRENCY_ID] ?: 0

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
    private fun onCallQueryGetClientBankAccountUseCase() {
        executeUseCase {
            queryGetClientBankAccountUseCase.invoke(
                user = user,
                idBrand = idBrand,
                idClient = idClient,
                idLoan = idLoanClient.toInt()
            ).collectLatest { result ->
                result.onSuccess { clientBankAccountList ->
                    onUIEvent(OnLoadingValueChange(false))
                    uiState = uiState.copy(
                        clientBankAccountList = clientBankAccountList
                    )
                }.onFailure {
                    onUIEvent(OnLoadingValueChange(false))
                    uiState = uiState.copy(
                        openDialog = DialogParameters(
                            description = it.getError() ?: "",
                            isActive = mutableStateOf(true)
                        )
                    )
                }.onLoading {
                    onUIEvent(OnLoadingValueChange(true))
                }
            }
        }
    }

    fun shouldDisplayExchangeRate() =
        when (idBrand) {
            Brand.CostaRica.id -> uiState.clientBankAccountSelected?.idCurrency != currencyId
            else -> false
        }

    private fun onClientBankAccountSelected(clientBankAccount: ClientBankAccount?) {
        uiState = uiState.copy(clientBankAccountSelected = clientBankAccount)
        if (shouldDisplayExchangeRate()) {
            onUIEvent(OnCallQueryGetExchangeRateCredit)
        } else {
            onUIEvent(OnShowDisbursementBottomSheet)
        }
    }

    private fun onSuccessProcessCreditExtension() {
        onUIEvent(OnLoadingValueChange(false))
        onUIEvent(OnHideDisbursementBottomSheet)
        if (idBrand == Brand.CostaRica.id) {
            // TODO navigate to voucher instead of this screen
            uiState = uiState.copy(
                isAlertResultVisible = true,
                alertResultIconResource = R.drawable.ic_success_symbol,
                alertResultTitleResource = R.string.disbursement_account_process_success_title,
                alertResultDescriptionResource = R.string.disbursement_account_process_success_description,
                alertButtonTextResource = R.string.understood
            )
        } else {
            uiState = uiState.copy(
                isAlertResultVisible = true,
                alertResultIconResource = R.drawable.ic_success_symbol,
                alertResultTitleResource = R.string.disbursement_account_process_success_title,
                alertResultDescriptionResource = R.string.disbursement_account_process_success_description,
                alertButtonTextResource = R.string.understood
            )
        }
    }

    private fun onMessageProcessCreditExtension(error: MessageError?) {
        onUIEvent(OnLoadingValueChange(false))
        onUIEvent(OnHideDisbursementBottomSheet)
        error?.let {
            uiState = uiState.copy(
                isAlertResultVisible = true,
                alertResultIconResource = R.drawable.ic_error_symbol,
                alertResultTitle = it.message ?: "",
                alertResultDescription = it.detail ?: "",
                alertButtonTextResource = R.string.disbursement_account_process_error_button
            )
        }
    }

    private fun onProcessCreditExtension() = executeUseCase {
        mutationProcessCreditExtensionDetailUseCase.invoke(
            user = identification?.toString() ?: "",
            pkUser = pkUser ?: 0,
            idBrand = idBrand,
            idFlowControl = fkFlowControl ?: 0,
            currency = currencyId?.getCurrency()?.disbursementValue ?: "",
            accountNumber = creditNumber ?: "",
            bankAccount = uiState.clientBankAccountSelected?.accountNumber ?: "",
            idBankAccount = uiState.clientBankAccountSelected?.id ?: "",
            idLoanForm = ID_LOAN_FORM_HARDCODED,
            loanForm = LOAN_FORM_HARDCODED,
            idLoanClient = idLoanClient.toInt(),
            phoneNumber = dataStorePreferences.getUserPhoneNumber().first(), // TODO validate
            userEmail = user
        ).collectLatest { result ->
            result.onSuccess {
                onUIEvent(OnSuccessProcessCreditExtension)
            }.onMessage {
                onUIEvent(OnMessageProcessCreditExtension(it?.messageError))
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }.onLoading {
                onUIEvent(OnLoadingValueChange(true))
            }
        }
    }

    private fun onCallQueryGetExchangeRate() = executeUseCase {
        queryGetExchangeRateCreditUseCase.invoke(
            idBrand,
            user,
            identification ?: "",
            currencyId?.toString() ?: "",
            uiState.clientBankAccountSelected?.idCurrency?.toString() ?: "",
            selectedAmount?.toDouble() ?: 0.0

        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(
                    exchangeRateLabel = it?.result?.exchangeRate ?: 0.0,
                    exchangeConvertedAmount = it?.result?.convertedAmount ?: 0.0
                )
                onUIEvent(OnLoadingValueChange(false))
                onUIEvent(OnShowDisbursementBottomSheet)
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    openDialog = DialogParameters(
                        description = it.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
            }
            result.onLoading { onUIEvent(OnLoadingValueChange(true)) }
        }
    }

    private fun onLoadingValueChange(loading: Boolean) {
        uiState = uiState.copy(isLoading = loading)
    }

    private fun onShowPaymentBottomSheet() {
        uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded))
    }

    private fun onHideDisbursementBottomSheet() {
        uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Hidden))
    }

    private fun onNavigateBack() = navigateBack(popTo = Screen.DisbursementAmountScreen.route, isRestart = false)

    private fun onNavigateBackHome() = navigateBack(popTo = Screen.HomeScreen.route, isRestart = false)

    private fun onNavigateToDisbursementAddAccount() =
        navigateTo(
            route = "${Screen.DisbursementAddAccountScreen.baseRoute}/$idBrand/$user/$pkUser/$idUserRequest"
        )

    fun getCurrentAmountFormatted() =
        "${currencyId?.getCurrency()?.symbol ?: ""}${
        selectedAmount?.stringToDoubleFormat(CreditAmountViewModel.CURRENCY_SEPARATOR.toString())
        }"

    fun getCurrentAmountExchangedFormatted() =
        "${uiState.clientBankAccountSelected?.idCurrency?.getCurrency()?.symbol ?: ""}${
        uiState.exchangeConvertedAmount.formattedTwoDecimalsNumber().toString().stringToDoubleFormat(CreditAmountViewModel.CURRENCY_SEPARATOR.toString())
        }"

    fun getExchangeRateFormatted() =
        "${currencyId?.getCurrency()?.symbol ?: ""}${
        uiState.exchangeRateLabel.toString().stringToDoubleFormat(CreditAmountViewModel.CURRENCY_SEPARATOR.toString())
        }"

    fun getQuotaTotalFormatted() =
        "${currencyId?.getCurrency()?.symbol ?: ""}${
        quotaTotal?.stringToDoubleFormat(CreditAmountViewModel.CURRENCY_SEPARATOR.toString()) ?: ""
        }"

    fun getQuotaNextDateFormatted() = getCardDateFormat(nextPaymentDate, BAR_DIVIDER_FORMAT_YEAR_TWO_DIGITS, API_DATE_FORMAT)

    data class UIState(
        // Interactions
        val titleResource: Int = R.string.empty,
        val clientBankAccountList: List<ClientBankAccount?>? = null,
        val clientBankAccountSelected: ClientBankAccount? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
        val isAlertResultVisible: Boolean = false,
        val alertResultIconResource: Int = R.drawable.ic_error_symbol,
        val alertResultTitle: String = "",
        val alertResultDescription: String = "",
        val alertResultTitleResource: Int = R.string.empty,
        val alertResultDescriptionResource: Int = R.string.empty,
        val alertButtonTextResource: Int = R.string.empty,
        val exchangeRateLabel: Double = 0.0,
        val exchangeConvertedAmount: Double = 0.0
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnNavigateBack -> onNavigateBack()
            is OnNavigateBackHome -> onNavigateBackHome()
            is OnCallQueryGetClientBankAccount -> onCallQueryGetClientBankAccountUseCase()
            is OnClientBankAccountSelected -> onClientBankAccountSelected(uiEvent.clientBankAccount)
            is OnNavigateToDisbursementAddAccount -> onNavigateToDisbursementAddAccount()
            is OnLoadingValueChange -> onLoadingValueChange(uiEvent.isLoading)
            is OnShowDisbursementBottomSheet -> onShowPaymentBottomSheet()
            is OnHideDisbursementBottomSheet -> onHideDisbursementBottomSheet()
            is OnCallQueryGetExchangeRateCredit -> onCallQueryGetExchangeRate()
            is OnSuccessProcessCreditExtension -> onSuccessProcessCreditExtension()
            is OnMessageProcessCreditExtension -> onMessageProcessCreditExtension(uiEvent.error)
            is OnProcessCreditExtension -> onProcessCreditExtension()
        }
    }

    sealed class UIEvent {
        object OnCallQueryGetClientBankAccount : UIEvent()
        class OnClientBankAccountSelected(val clientBankAccount: ClientBankAccount?) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnNavigateBackHome : UIEvent()
        object OnNavigateToDisbursementAddAccount : UIEvent()
        data class OnLoadingValueChange(val isLoading: Boolean) : UIEvent()
        object OnShowDisbursementBottomSheet : UIEvent()
        object OnHideDisbursementBottomSheet : UIEvent()
        object OnCallQueryGetExchangeRateCredit : UIEvent()
        object OnSuccessProcessCreditExtension : UIEvent()
        object OnProcessCreditExtension : UIEvent()
        data class OnMessageProcessCreditExtension(val error: MessageError?) : UIEvent()
    }

    companion object {
        private const val ID_LOAN_FORM_HARDCODED = 4 // TODO Change to 1-4 depending on preferences user previously selected (new HU)
        private const val LOAN_FORM_HARDCODED = "Transferencia" // TODO Change to Transferencia-PEX depending on preferences user previously selected (new HU)
    }
}
