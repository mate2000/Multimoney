package com.multimoney.multimoney.presentation.ui.smart.transfer.transfer365.accountlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QueryACHTransferFavoriteListUseCase
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.accountsmart.Transfer365Account
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
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class Transfer365AccountListViewModel @Inject constructor(
    private val queryACHTransferFavoriteListUseCase: QueryACHTransferFavoriteListUseCase,
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
        smartAccount = savedStateHandle[SMART_ACCOUNT]
        user = savedStateHandle[USER] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
    }

    private fun getACHAccountList() {
        executeUseCase {
            queryACHTransferFavoriteListUseCase.invoke(
                user = user,
                idBrand = idBrand,
                isFavorite = true,
                identificationNumber = identification.orEmpty()
            ).collectLatest { result ->
                result.onSuccess { favoriteResult ->
                    if (favoriteResult?.data.isNullOrEmpty()) {
                        popAndNavigateTo(
                            "${Screen.SmartAdd365AccountScreen.baseRoute}/$idBrand/$user/${
                                encodeData(
                                    smartAccount
                                )
                            }/${SmartTransferTypes.SmartToOtherBank.id}/${Screen.SmartSelectSendingTypeScreen.baseRoute}",
                            Screen.SmartACHAccountsListScreen.route
                        )
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            accounts = favoriteResult?.data ?: listOf()
                        )
                    }
                }
                result.onFailure { onFailure(it) }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
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

    private fun onNavigateBack() =
        navigateBack(popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false)

    private fun navigateToAddACHAccount() {
        navigateTo(
            "${Screen.SmartAdd365AccountScreen.baseRoute}/$idBrand/$user/${
                encodeData(
                    smartAccount
                )
            }/${SmartTransferTypes.SmartToOtherBank.id}/${Screen.SmartACHAccountsListScreen.baseRoute}"
        )
    }

    private fun onAccountClick(account: ACHAccount?) {
        val transfer = Transfer365Account(
            accountId = account?.accountForAchTransferId,
            accountNumber = account?.accountNumber,
            bankId = account?.idBank.toString(),
            bankName = account?.destinationBankDescription.orEmpty(),
            accountTypeId = account?.idTypeAccount.toString(),
            isFavorite = account?.isFavorite == true
        )
        navigateTo(
            "${Screen.SmartTransfer365EditAmountScreen.baseRoute}/${
                encodeData(smartAccount)
            }/${encodeData(transfer)}/${SmartTransferTypes.SmartToOtherBank.id}/${Screen.SmartAdd365AccountScreen}"
        )
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var accounts: List<ACHAccount?> = listOf()
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnAddAccountClick -> navigateToAddACHAccount()
            is UIEvent.OnNavigateBack -> onNavigateBack()
            is UIEvent.OnAccountClick -> onAccountClick(uiEvent.account)
            is UIEvent.OnGetAccountList -> getACHAccountList()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnAddAccountClick : UIEvent()
        object OnGetAccountList : UIEvent()
        data class OnAccountClick(val account: ACHAccount?) : UIEvent()
    }
}