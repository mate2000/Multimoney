package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.cr

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QueryACHTransferFavoriteGetUseCase
import com.multimoney.domain.interaction.accountsmart.QueryACHTransferFavoriteListUseCase
import com.multimoney.domain.interaction.accountsmart.QueryListSavedSACAccountsUseCase
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.LocalSACAccount
import com.multimoney.domain.model.accountsmart.PhoneSmart
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartTransferFavoriteCRViewModel @Inject constructor(
    private val queryListSavedSACAccountsUseCase: QueryListSavedSACAccountsUseCase,
    private val queryACHTransferFavoriteListUseCase: QueryACHTransferFavoriteListUseCase,
    private val queryACHTransferFavoriteGetUseCase: QueryACHTransferFavoriteGetUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var identification: String? = ""
    private var smartAccount: SmartAccountID? = null

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        smartAccount = savedStateHandle[SMART_ACCOUNT]
    }

    private fun getACHFavoriteAccounts() = executeUseCase {
        queryACHTransferFavoriteListUseCase.invoke(
            user = user,
            identificationNumber = identification ?: "",
            idBrand = idBrand,
            isFavorite = true
        ).collectLatest { result ->
            result.onSuccess { ACHFavoriteAccountList ->
                ACHFavoriteAccountList?.data?.let { ACHFavoriteAccounts ->
                    uiState =
                        uiState.copy(
                            isLoading = false,
                            aCHFavoriteAccountList = ACHFavoriteAccounts.sortedBy { ACHFavoriteAccount ->
                                ACHFavoriteAccount?.description.orEmpty()
                            }
                        )
                }
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false)
                onFailure(it)
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun getLocalSACFavoriteAccounts() = executeUseCase {
        queryListSavedSACAccountsUseCase.invoke(
            user = user,
            idClient = smartAccount?.customerId ?: 0L,
            idBrand = idBrand,
            isFavorite = true
        ).collectLatest { result ->
            result.onSuccess { accountsWrapper ->
                accountsWrapper?.accounts?.let { accountList ->
                    uiState =
                        uiState.copy(
                            isLoading = false,
                            localFavoriteList = accountList.sortedBy { localAccount ->
                                localAccount.accountName.orEmpty()
                            }
                        )
                }
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false)
                onFailure(it)
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun getFavoritesListForCR() {
        getLocalSACFavoriteAccounts()
        getACHFavoriteAccounts()
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(
            isLoading = false, openDialog = DialogParameters(
                description = error.getError() ?: "", isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateBack() {
        navigateBack(
            popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false
        )
    }

    private fun onShowOptionsForACHClick(selectedACHFavorite: ACHAccount?) {
        // TODO REV-346
    }

    private fun onShowOptionsForLocalClick(selectedLocalFavorite: LocalSACAccount?) {
        // TODO REV-346
    }

    private fun onACHFavoriteClick(selectedAccount: ACHAccount?) {
        executeUseCase {
            queryACHTransferFavoriteGetUseCase.invoke(
                user,
                idBrand,
                selectedAccount?.accountForAchTransferId ?: 0
            ).collectLatest { result ->
                result.onSuccess { achAccountFull ->
                    val ibanAccount = encodeData(
                        IbanAccountID(
                            bank = achAccountFull?.destinationBankDescription,
                            clientIdentification = achAccountFull?.identificationNumberAccount,
                            sinpeAccount = achAccountFull?.accountNumber,
                            currencyId = achAccountFull?.destinationAccountCurrencyId,
                            nameAccount = achAccountFull?.titularName
                        )
                    )
                    uiState = uiState.copy(isLoading = false)
                    navigateTo(
                        "${Screen.SmartTransferAmountScreen.baseRoute}/" +
                                "${encodeData(smartAccount)}/$ibanAccount/" +
                                "${SmartTransferTypes.SmartToIban.id}/${Screen.SmartTransferFavoriteAccountCRScreen.baseRoute}"
                    )
                }
                result.onFailure { onFailure(it) }
                result.onLoading { uiState = uiState.copy(isLoading = true) }
            }
        }
    }

    private fun onLocalFavoriteClick(selectedAccount: LocalSACAccount?) {
        val account = PhoneSmart(
            number = selectedAccount?.phoneNumber,
            titular = selectedAccount?.accountName,
            bankName = null,
            identification = selectedAccount?.identification,
            accountNumber = selectedAccount?.accountNumber,
            email = selectedAccount?.email,
            idCurrency = selectedAccount?.idCurrency.toString(),
            currency = selectedAccount?.currency,
            ibanNumber = selectedAccount?.ibanNumber
        )
        navigateTo(
            "${Screen.MyContactsTransferAmountScreen.baseRoute}/" +
                    "${encodeData(smartAccount)}/${encodeData(account)}/" +
                    "${SmartTransferTypes.SmartToContact.id}/$idBrand/" +
                    Screen.SmartTransferFavoriteAccountCRScreen.baseRoute
        )
    }

    private fun onNavigateToHome() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.COLLAPSED
        )
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var aCHFavoriteAccountList: List<ACHAccount?> = listOf(),
        var localFavoriteList: List<LocalSACAccount?> = listOf()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnACHOptionsClick -> onShowOptionsForACHClick(uiEvent.aCHFavorite)
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnACHFavoriteClick -> onACHFavoriteClick(uiEvent.aCHFavorite)
            is UIEvent.OnNavigateToHome -> onNavigateToHome()
            is UIEvent.GetFavoritesLists -> getFavoritesListForCR()
            is UIEvent.OnLocalFavoriteClick -> onLocalFavoriteClick(uiEvent.localAccount)
            is UIEvent.OnLocalOptionsClick -> onShowOptionsForLocalClick(uiEvent.localFavorite)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateToHome : UIEvent()
        object GetFavoritesLists : UIEvent()
        data class OnACHOptionsClick(val aCHFavorite: ACHAccount?) : UIEvent()
        data class OnLocalOptionsClick(val localFavorite: LocalSACAccount?) : UIEvent()
        data class OnACHFavoriteClick(val aCHFavorite: ACHAccount?) : UIEvent()
        data class OnLocalFavoriteClick(val localAccount: LocalSACAccount?) : UIEvent()
    }
}