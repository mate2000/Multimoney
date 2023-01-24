package com.multimoney.multimoney.presentation.ui.smart.transfer.otherbanks.addaccount

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationAddACHAccountUseCase
import com.multimoney.domain.interaction.accountsmart.QueryBankListTransfer365UseCase
import com.multimoney.domain.interaction.accountsmart.QuerySmartAccountTypeUseCase
import com.multimoney.domain.interaction.security.QueryCatalogDocumentTypeUseCase
import com.multimoney.domain.model.accountsmart.BankTransfer365
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountType
import com.multimoney.domain.model.security.CatalogDocument
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.TRANSFER_TYPE
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.MAX_SMART_ACCOUNT_DIGITS
import com.multimoney.multimoney.presentation.util.MIN_SMART_ACCOUNT_DIGITS
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.PhoneCountryCode
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import com.multimoney.multimoney.presentation.util.isPhoneNumberValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartAddOtherBankAccountViewModel @Inject constructor(
    private val queryCatalogDocumentTypeUseCase: QueryCatalogDocumentTypeUseCase,
    private val querySmartAccountTypeUseCase: QuerySmartAccountTypeUseCase,
    private val queryBankListTransfer365UseCase: QueryBankListTransfer365UseCase,
    private val mutationAddACHAccountUseCase: MutationAddACHAccountUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    //Stateless
    private var idBrand = 0
    private var user = ""
    private var smartAccount: SmartAccountID? = null
    var transferType: Int = 0

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        smartAccount = savedStateHandle[SMART_ACCOUNT]
        transferType = savedStateHandle[TRANSFER_TYPE] ?: 0
        if (transferType == SmartTransferTypes.SmartToOtherBank.id) {
            uiState = uiState.copy(
                screenTitle = R.string.smart_other_bank_transfer_add_title,
                screenSubtitle = R.string.empty,
                typeAccountLabel = R.string.smart_other_bank_transfer_add_account_type_label
            )
        } else if (transferType == SmartTransferTypes.SmartToMobile.id) {
            uiState = uiState.copy(
                screenTitle = R.string.smart_transfer_365_mobile_add_title,
                screenSubtitle = R.string.smart_transfer_365_mobile_add_subtitle,
                typeAccountLabel = R.string.smart_transfer_365_mobile_add_account_type_label
            )
        }
    }

    private fun getDropdownLists() {
        getDocumentTypes()
        getBanks()
        getAccountTypes()
    }

    private fun getAccountTypes() = executeUseCase {
        querySmartAccountTypeUseCase.invoke(
            idBrand = idBrand,
            user = user
        ).collectLatest { result ->
            result.onSuccess { types ->
                uiState = uiState.copy(
                    isLoading = false,
                    accountTypeList = types?.typeList ?: listOf()
                )
            }
            result.onFailure { onFailure(it) }
            result.onLoading { uiState = uiState.copy(isLoading = true) }
        }
    }

    private fun getBanks() = executeUseCase {
        queryBankListTransfer365UseCase.invoke(
            idBrand = idBrand,
            user = user
        ).collectLatest { result ->
            result.onSuccess { banks ->
                uiState = uiState.copy(
                    isLoading = false,
                    bankList = banks?.bankList ?: listOf()
                )
            }
            result.onFailure { onFailure(it) }
            result.onLoading { uiState = uiState.copy(isLoading = true) }
        }
    }

    private fun getDocumentTypes() = executeUseCase {
        queryCatalogDocumentTypeUseCase.invoke(
            idBrand = idBrand,
            user = user,
            isTransferIdentification = 1
        ).collectLatest { result ->
            result.onSuccess { catalog ->
                uiState = uiState.copy(
                    isLoading = false,
                    documentList = catalog?.catalogDocument ?: listOf(),
                    document = catalog?.catalogDocument?.firstOrNull(),
                    documentLength = getDocumentLength(catalog?.catalogDocument?.firstOrNull()?.format.orEmpty())
                )
            }
            result.onFailure { onFailure(it) }
            result.onLoading { uiState = uiState.copy(isLoading = true) }
        }
    }

    private fun onBankSelected(newBank: String) {
        val bank = uiState.bankList.find { it.bankName == newBank }
        uiState = uiState.copy(bank = bank)
        validateForm()
    }

    private fun onAccountTypeSelected(newType: String) {
        val type = uiState.accountTypeList.find { it?.typeName == newType }
        uiState = uiState.copy(type = type)
        validateForm()
    }

    private fun onAccountNumberChanged(number: String) {
        if (number.isDigitsOnly()) {
            uiState = uiState.copy(accountNumber = number)
        }
    }

    private fun onDocumentNumberChanged(documentNumber: String) {
        if (documentNumber.isDigitsOnly()) {
            uiState = uiState.copy(documentNumber = documentNumber)
        }
    }

    private fun onDocumentTypeSelected(newDocument: String) {
        val document = uiState.documentList.find { it.description == newDocument }
        uiState = uiState.copy(
            document = document,
            documentLength = getDocumentLength(document?.format.orEmpty())
        )
        validateForm()
    }

    private fun getDocumentLength(format: String): Int {
        return if (format.isNotEmpty()) {
            format.count { format.last() == it }
        } else {
            Int.MAX_VALUE
        }
    }

    private fun onNamesChanged(newNames: String) {
        uiState = uiState.copy(names = newNames)
        validateForm()
    }

    private fun onLastNamesChanged(newLastNames: String) {
        uiState = uiState.copy(lastNames = newLastNames)
        validateForm()
    }

    private fun isDocumentValid() {
        uiState = if (uiState.documentNumber.length == uiState.documentLength) {
            uiState.copy(personalIdError = Pair(false, R.string.empty))
        } else {
            uiState.copy(personalIdError = Pair(true, R.string.smart_iban_register_account_error))
        }
        validateForm()
    }

    private fun isAccountNumberValid() {
        uiState = uiState.copy(
            isAccountNumberError = uiState.accountNumber.length !in MIN_SMART_ACCOUNT_DIGITS..MAX_SMART_ACCOUNT_DIGITS
        )
        validateForm()
    }

    private fun onAddFavoriteValueChange(isChecked: Boolean) {
        uiState = uiState.copy(isFavorite = isChecked)
    }

    private fun onPhoneNumberChanged(phone: String) {
        if (phone.isDigitsOnly()) {
            uiState = uiState.copy(phoneNumber = phone)
        }
    }

    private fun validatePhoneNumber() {
        val isValid = isPhoneNumberValid(
            uiState.phoneNumber,
            "${PhoneCountryCode.EL_SALVADOR.code}${uiState.phoneNumber}",
            Brand.ElSalvador.countryCode,
            PhoneNumberUtil.PhoneNumberType.MOBILE
        )
        uiState = uiState.copy(isPhoneNumberError = isValid.not())
        validateForm()
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(
            isLoading = false,
            openDialog = DialogParameters(
                description = error.getError() ?: "",
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun validateForm() {
        uiState = uiState.copy(
            enableButton = when {
                uiState.type == null -> false
                uiState.personalIdError.first -> false
                transferType == SmartTransferTypes.SmartToOtherBank.id &&
                        uiState.isAccountNumberError -> false
                transferType == SmartTransferTypes.SmartToOtherBank.id &&
                        uiState.accountNumber.isEmpty() -> false
                uiState.names.isEmpty() -> false
                uiState.lastNames.isEmpty() -> false
                uiState.document == null -> false
                uiState.bank == null -> false
                uiState.documentNumber.isEmpty() -> false
                transferType == SmartTransferTypes.SmartToMobile.id &&
                        uiState.phoneNumber.isEmpty() -> false
                transferType == SmartTransferTypes.SmartToMobile.id &&
                        uiState.isPhoneNumberError -> false
                else -> true
            }
        )
    }

    private fun onContinueClick() {
        if (transferType == SmartTransferTypes.SmartToOtherBank.id) {
            saveOtherBankAccount()
        } else if (transferType == SmartTransferTypes.SmartToMobile.id) {
            // Todo Navigation to edit amount
            uiState = uiState.copy(
                openDialog = DialogParameters(
                    isActive = mutableStateOf(true),
                    description = "TBD: Navegar a pantalla de monto REV-1463",
                    titleResource = R.string.info
                )
            )
        }
    }

    private fun saveOtherBankAccount() {
        executeUseCase {
            mutationAddACHAccountUseCase.invoke(
                idBrand = idBrand,
                user = user,
                accountNumber = uiState.accountNumber,
                titularName = "${uiState.names} ${uiState.lastNames}",
                isFavorite = uiState.isFavorite,
                typeAccountId = uiState.type?.typeId?.toIntOrNull() ?: 0,
                destinationBankId = uiState.bank?.bankId?.toInt() ?: 0,
                description = uiState.bank?.bankName.orEmpty(),
                identificationNumber = uiState.documentNumber,
                identificationTypeAccount = uiState.document?.idDocument ?: 0
            ).collectLatest { result ->
                result.onSuccess { account ->
                    // Todo navigate to edit amount REV-1463
                    uiState = uiState.copy(
                        isLoading = false,
                        openDialog = DialogParameters(
                            isActive = mutableStateOf(true),
                            description = "Cuenta guardada. TBD: Navegar a pantalla de monto REV-1463",
                            titleResource = R.string.info
                        )
                    )
                }
                result.onFailure { onFailure(it) }
                result.onLoading { uiState = uiState.copy(isLoading = true) }
            }
        }
    }

    private fun onNavigateBack() =
        navigateBack(
            popTo = Screen.SmartSelectSendingTypeScreen.route,
            isRestart = false
        )

    private fun onNavigateToHome() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = false
        )

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        val isLoading: Boolean = false,
        val accountTypeList: List<SmartAccountType?> = listOf(),
        val type: SmartAccountType? = null,
        val document: CatalogDocument? = null,
        val documentList: List<CatalogDocument> = listOf(),
        val documentNumber: String = "",
        val documentLength: Int = 0,
        val accountNumber: String = "",
        val isAccountNumberError: Boolean = false,
        val bank: BankTransfer365? = null,
        val bankList: List<BankTransfer365> = listOf(),
        val personalIdError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val enableButton: Boolean = false,
        val names: String = "",
        val lastNames: String = "",
        val isFavorite: Boolean = false,
        val phoneNumber: String = "",
        val isPhoneNumberError: Boolean = false,
        val screenTitle: Int = R.string.smart_other_bank_transfer_add_title,
        val screenSubtitle: Int = R.string.smart_transfer_365_mobile_add_subtitle,
        val typeAccountLabel: Int = R.string.smart_other_bank_transfer_add_account_type_label
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnDocumentTypeSelected -> onDocumentTypeSelected(uiEvent.type)
            is UIEvent.OnDocumentChanged -> onDocumentNumberChanged(uiEvent.document)
            is UIEvent.OnAccountNumberChanged -> onAccountNumberChanged(uiEvent.number)
            is UIEvent.OnAccountTypeSelected -> onAccountTypeSelected(uiEvent.type)
            is UIEvent.OnBankSelected -> onBankSelected(uiEvent.bank)
            is UIEvent.OnValidateDocument -> isDocumentValid()
            is UIEvent.OnContinueClick -> onContinueClick()
            is UIEvent.OnGetListValues -> getDropdownLists()
            is UIEvent.OnValidateAccountNumber -> isAccountNumberValid()
            is UIEvent.OnNamesChanged -> onNamesChanged(uiEvent.names)
            is UIEvent.OnLastNamesChanged -> onLastNamesChanged(uiEvent.lastNames)
            is UIEvent.OnNavigateHome -> onNavigateToHome()
            is UIEvent.OnAddFavoriteValueChange -> onAddFavoriteValueChange(uiEvent.isChecked)
            is UIEvent.OnPhoneChanged -> onPhoneNumberChanged(uiEvent.phone)
            is UIEvent.OnValidatePhoneNumber -> validatePhoneNumber()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnValidateDocument : UIEvent()
        object OnValidateAccountNumber : UIEvent()
        object OnValidatePhoneNumber : UIEvent()
        object OnContinueClick : UIEvent()
        object OnGetListValues : UIEvent()
        object OnNavigateHome : UIEvent()
        data class OnDocumentTypeSelected(val type: String) : UIEvent()
        data class OnBankSelected(val bank: String) : UIEvent()
        data class OnAccountTypeSelected(val type: String) : UIEvent()
        data class OnAccountNumberChanged(val number: String) : UIEvent()
        data class OnNamesChanged(val names: String) : UIEvent()
        data class OnLastNamesChanged(val lastNames: String) : UIEvent()
        data class OnDocumentChanged(val document: String) : UIEvent()
        data class OnPhoneChanged(val phone: String) : UIEvent()
        data class OnAddFavoriteValueChange(val isChecked: Boolean) : UIEvent()
    }
}