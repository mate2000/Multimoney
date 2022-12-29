package com.multimoney.multimoney.presentation.ui.smart.transfer.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationSaveSinpeAccountUseCase
import com.multimoney.domain.interaction.security.QueryCatalogDocumentTypeUseCase
import com.multimoney.domain.interaction.security.QueryValidateBankAccountUseCase
import com.multimoney.domain.model.security.CatalogType
import com.multimoney.domain.model.security.ValidateAccount
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CURRENCY
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.credit.addibanaccount.AddIbanAccountViewModel
import com.multimoney.multimoney.presentation.util.capitalized
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.isEmailValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SmartTransferRegisterIbanViewModel @Inject constructor(
    private val queryCatalogDocumentTypeUseCase: QueryCatalogDocumentTypeUseCase,
    private val queryValidateBankAccountUseCase: QueryValidateBankAccountUseCase,
    private val mutationSaveSinpeAccountUseCase: MutationSaveSinpeAccountUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set
    var closeKeyboard by mutableStateOf(false)

    // Interactions
    private var onSuccessCatalogDocumentType: CatalogType? = null
    private var validateAccount: ValidateAccount? = null
    private var documentLength = 0

    // Stateless
    private var user: String?
    private var idBrand: Int?
    private var identification: String?
    private var previousScreen: String?
    private var idClient: Int?
    private var idCurrency: Int?

    init {
        user = savedStateHandle[USER]
        idBrand = savedStateHandle[ID_BRAND]
        identification = savedStateHandle[IDENTIFICATION]
        previousScreen = savedStateHandle[PREVIOUS_SCREEN]
        idClient = savedStateHandle[ID_CLIENT]
        idCurrency = savedStateHandle[ID_CURRENCY]
    }

    private fun onQueryDocumentList() {
        idBrand?.let { callQueryCatalogDocumentType(it) }
    }

    private fun onAccountValueValueChange(bankAccount: String) {
        if (bankAccount.isDigitsOnly() && bankAccount.length <= IBAN_MAX_LENGTH) {
            uiState = uiState.copy(
                ibanAccountNumber = bankAccount,
                accountError = if (bankAccount.length < AddIbanAccountViewModel.IBAN_MAX_LENGTH) {
                    Pair(true, R.string.iban_account_error)
                } else {
                    Pair(false, R.string.empty)
                },
                accountValidationError = null
            )
            if (bankAccount.length == AddIbanAccountViewModel.IBAN_MAX_LENGTH) {
                validateIbanAccount()
            } else {
                isFormValid()
            }
        }
    }

    private fun validateIbanAccount() = executeUseCase {
        uiState = uiState.copy(accountInformation = Pair(true, R.string.iban_account_loading))
        queryValidateBankAccountUseCase(
            account = "${Brand.CostaRica.iban}${uiState.ibanAccountNumber}",
            identification = identification.orEmpty(),
            queryType = "",
            user = user.orEmpty(),
            idBrand = idBrand ?: 0
        ).collectLatest {
            it.onSuccess { account ->
                account?.let { response ->
                    when (response.responseCode) {
                        AddIbanAccountViewModel.IS_VALID -> {
                            validateAccount = response
                            uiState = uiState.copy(
                                accountInformation = Pair(false, R.string.empty)
                            )
                        }
                        AddIbanAccountViewModel.HAS_ERRORS -> {
                            uiState =
                                uiState.copy(
                                    accountError = Pair(false, R.string.empty),
                                    accountInformation = Pair(false, R.string.empty),
                                    accountValidationError = Pair(true,
                                        response.responseMessage.capitalized())
                                )
                        }
                    }
                }
                isFormValid()
            }.onFailure { error ->
                uiState = uiState.copy(
                    accountInformation = Pair(false, R.string.empty),
                    openDialog = DialogParameters(
                        description = error.getError() ?: "",
                        isActive = mutableStateOf(true)
                    )
                )
                onFailure(error)
                isFormValid()
            }
        }
    }

    private fun callQueryCatalogDocumentType(idBrand: Int) {
        viewModelScope.launch {
            queryCatalogDocumentTypeUseCase(
                idBrand,
                ""
            ).collectLatest { result ->
                result.onSuccess {
                    onSuccessCatalogDocumentType = it
                    val documentList: ArrayList<String> = arrayListOf()
                    it?.catalogDocument?.forEach { document ->
                        documentList.add(document.description)
                    }
                    uiState = uiState.copy(documentList = documentList)
                    if (uiState.identificationValueType.isEmpty().not()) {
                        getDocumentLength(uiState.identificationValueType, true)
                    } else {
                        uiState = uiState.copy(identificationValueType = documentList.first())
                        getDocumentLength(uiState.identificationValueType)
                    }
                    uiState = uiState.copy(isLoading = false)
                }
                result.onFailure { error ->
                    uiState = uiState.copy(isLoading = false)
                    onSuccessCatalogDocumentType = null
                    onFailure(error)
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    private fun getDocumentLength(documentType: String, isFromBackend: Boolean = false) {
        onSuccessCatalogDocumentType?.catalogDocument?.forEach { documentCatalog ->
            if (documentCatalog.description == documentType) {
                uiState = uiState.copy(
                    documentFormat = documentCatalog.format,
                    identificationValueType = documentType
                )
                documentLength = if (documentCatalog.format.isNotEmpty()) {
                    documentCatalog.format.count { documentCatalog.format.last() == it }
                } else {
                    Int.MAX_VALUE
                }
            }
        }
        if (isFromBackend.not()) {
            cleanUIForIdentification()
        }
    }

    private fun cleanUIForIdentification() {
        uiState = uiState.copy(documentNumber = "")
    }

    private fun onIdentificationTypeValueChange(documentType: String) {
        getDocumentLength(documentType)
        isFormValid()
    }

    private fun onIdentificationValueChange(identificationValue: String) {
        if (identificationValue.length <= documentLength) {
            uiState = uiState.copy(documentNumber = identificationValue)
        }
        isFormValid()
    }

    private fun onValidateDocument(document: String) {
        uiState = uiState.copy(
            personalIdError =
            if (uiState.documentNumber.isNotBlank() && document.length < documentLength) {
                Pair(
                    true,
                    R.string.smart_iban_register_account_error
                )
            } else {
                Pair(
                    false,
                    R.string.smart_iban_register_account_error
                )
            }
        )
        isFormValid()
    }

    private fun onAddFavoriteValueChange(isChecked: Boolean) {
        uiState = uiState.copy(addFavorite = isChecked)
    }

    private fun onFavoriteNameValueChange(favoriteName: String) {
        uiState = uiState.copy(favoriteName = favoriteName)
    }

    private fun onEmailNameValueChange(email: String) {
        uiState = uiState.copy(email = email)
        clearUserEmailError()
        isFormValid()
    }

    private fun isUserEmailValid() {
        if (isEmailValid(uiState.email).not()) {
            uiState =
                uiState.copy(userEmailError = Pair(true, R.string.smart_iban_register_email_error))
        }
        isFormValid()
    }

    private fun clearUserEmailError() {
        uiState = uiState.copy(userEmailError = Pair(false, R.string.error_empty))
    }

    private fun isFormValid() {
        uiState = uiState.copy(
            isFormValid = when {
                uiState.ibanAccountNumber.isBlank() -> false
                uiState.accountError.first -> false
                uiState.accountValidationError?.first == true -> false
                uiState.personalIdError.first -> false
                isEmailValid(uiState.email).not() -> false
                else -> true
            }
        )
    }

    private fun onContinueButtonClick() = executeUseCase {
        mutationSaveSinpeAccountUseCase(
            user = user ?: "",
            idBrand = idBrand ?: Brand.CostaRica.id,
            identification = uiState.documentNumber,
            accountNumber = Brand.CostaRica.iban.plus(uiState.ibanAccountNumber),
            idCurrency = idCurrency?.toLong() ?: 0L,
            nameAccount = uiState.favoriteName.ifBlank { user ?: "" },
            country = Brand.CostaRica.countryCode,
            idAccount = null,
            option = null
        ).collectLatest {
            it.onSuccess {
                uiState = uiState.copy(isLoading = false)
                onNavigateToSendMoney()
            }.onFailure { error ->
                uiState = uiState.copy(isLoading = false)
                onFailure(error)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
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

    private fun onNavigateBack() {
        popAndNavigateTo(
            route = Screen.HomeScreen.route,
            popTo = Screen.TransferIbanAccountScreen.route
        )
    }

    private fun onNavigateToSendMoney() {
        // TODO Implement send money navigation
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        val ibanAccountNumber: String = "",
        val accountError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val accountInformation: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val accountValidationError: Pair<Boolean, String>? = Pair(false, ""),
        val documentList: ArrayList<String> = arrayListOf(),
        val documentNumber: String = "",
        val documentFormat: String = "",
        val identificationValueType: String = "",
        val personalIdError: Pair<Boolean, Int> = Pair(false, R.string.smart_iban_register_account_error),
        val email: String = "",
        val userEmailError: Pair<Boolean, Int> = Pair(false, R.string.empty),
        val favoriteName: String = "",
        val addFavorite: Boolean = false,
        val isFormValid: Boolean = false,
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnAccountValueChange -> onAccountValueValueChange(uiEvent.accountNumber)
            is UIEvent.OnIdentificationValueChange -> onIdentificationValueChange(uiEvent.identification)
            is UIEvent.OnIdentificationTypeChange -> onIdentificationTypeValueChange(uiEvent.identificationType)
            is UIEvent.OnQueryDocumentList -> onQueryDocumentList()
            is UIEvent.OnValidateDocument -> onValidateDocument(uiEvent.document ?: "")
            is UIEvent.OnAddFavoriteValueChange -> onAddFavoriteValueChange(uiEvent.isChecked)
            is UIEvent.OnFavoriteNameValueChange -> onFavoriteNameValueChange(uiEvent.favoriteName)
            is UIEvent.OnEmailNameValueChange -> onEmailNameValueChange(uiEvent.email)
            is UIEvent.OnValidateUserEmail -> isUserEmailValid()
            is UIEvent.OnContinueButtonClick -> onContinueButtonClick()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnQueryDocumentList : UIEvent()
        object OnValidateUserEmail : UIEvent()
        object OnContinueButtonClick : UIEvent()
        data class OnAccountValueChange(val accountNumber: String) : UIEvent()
        data class OnIdentificationValueChange(val identification: String) : UIEvent()
        data class OnIdentificationTypeChange(val identificationType: String) : UIEvent()
        data class OnValidateDocument(val document: String? = null) : UIEvent()
        data class OnAddFavoriteValueChange(val isChecked: Boolean) : UIEvent()
        data class OnFavoriteNameValueChange(val favoriteName: String) : UIEvent()
        data class OnEmailNameValueChange(val email: String) : UIEvent()
    }

    companion object {
        const val IBAN_MAX_LENGTH = 20
        const val FORMAT_VALUE = '0'
    }
}