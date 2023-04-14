package com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.MutationACHTransferFavoriteDeleteUseCase
import com.multimoney.domain.interaction.accountsmart.QueryACHTransferFavoriteGetUseCase
import com.multimoney.domain.interaction.accountsmart.QueryACHTransferFavoriteListUseCase
import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.accountsmart.ACHAccountFull
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.EDIT_SUCCESS
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.ID_CLIENT
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.BaseEvent.OnHideAccountOptionsBottomSheet
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.BaseEvent.OnShowAccountOptionsBottomSheet
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnAddAccountClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnCallListSinpeAccounts
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnDeleteAccount
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnEditAccount
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnHideAccountOptions
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnHideToast
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.iban.account.SmartTransferIbanViewModel.UIEvent.OnShowAccountOptions
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.catalog.SmartTransferTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SmartTransferIbanViewModel @Inject constructor(
    private val queryACHTransferFavoriteListUseCase: QueryACHTransferFavoriteListUseCase,
    private val queryACHTransferFavoriteGetUseCase: QueryACHTransferFavoriteGetUseCase,
    private val mutationACHTransferFavoriteDeleteUseCase: MutationACHTransferFavoriteDeleteUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: String = ""
    private var idClient: String = ""
    private var identification: String? = ""
    private var smartAccount: SmartAccountID? = null
    private var selectedAccount: ACHAccount? = null
    private var editSuccess: Boolean = false

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: ""
        idClient = savedStateHandle[ID_CLIENT] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        smartAccount = savedStateHandle[SMART_ACCOUNT]
        editSuccess = savedStateHandle[EDIT_SUCCESS] ?: false
    }

    private fun onCallListSinpeAccounts() = executeUseCase {
        queryACHTransferFavoriteListUseCase.invoke(
            user = user,
            idBrand = idBrand.toIntOrNull() ?: 0,
            isFavorite = true,
            identificationNumber = identification.orEmpty()
        ).collectLatest { result ->
            result.onSuccess { achResult ->
                uiState = uiState.copy(sinpeAccountList = listOf()) // Reset list
                achResult?.data?.let { accounts ->
                    if (accounts.isEmpty()) {
                        navigateToAddIbanAccount()
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            sinpeAccountList = uiState.sinpeAccountList + accounts
                        )
                    }
                }
                callNoFavoritesListSinpeAccount()
            }
            result.onFailure { onFailure(it) }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun callNoFavoritesListSinpeAccount() = executeUseCase {
        queryACHTransferFavoriteListUseCase.invoke(
            user = user,
            idBrand = idBrand.toIntOrNull() ?: 0,
            isFavorite = false,
            identificationNumber = identification.orEmpty()
        ).collectLatest { result ->
            result.onSuccess { achResult ->
                achResult?.data?.let { accounts ->
                    if (accounts.isEmpty()) {
                        navigateToAddIbanAccount()
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            sinpeAccountList = uiState.sinpeAccountList + accounts
                        )
                    }
                }
            }
            result.onFailure { onFailure(it) }
            result.onLoading {
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

    private fun onNavigateBack() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.COLLAPSED
        )

    private fun navigateToAddIbanAccount() {
        navigateTo(
            "${Screen.SmartTransferRegisterIbanScreen.baseRoute}/$user/$idBrand/$identification/${Screen.SmartTransferIbanAccountScreen.baseRoute}/$idClient/${
            encodeData(
                smartAccount
            )
            }"
        )
    }

    private fun onAccountClick(selectedAccount: ACHAccount?) {
        selectedAccount?.let {
            executeUseCase {
                queryACHTransferFavoriteGetUseCase.invoke(
                    user,
                    idBrand.toIntOrNull() ?: 0,
                    selectedAccount.accountForAchTransferId ?: 0
                ).collectLatest { result ->
                    result.onSuccess { achAccountFull ->
                        uiState = uiState.copy(isLoading = false)
                        navigateToIbanAmount(achAccountFull)
                    }
                    result.onFailure { onFailure(it) }
                    result.onLoading { uiState = uiState.copy(isLoading = true) }
                }
            }
        }
    }

    private fun navigateToIbanAmount(fullAccount: ACHAccountFull?) {
        val ibanAccount = encodeData(
            IbanAccountID(
                bank = fullAccount?.destinationBankDescription,
                clientIdentification = fullAccount?.identificationNumberAccount,
                sinpeAccount = fullAccount?.accountNumber,
                currencyId = fullAccount?.destinationAccountCurrencyId,
                nameAccount = fullAccount?.titularName
            )
        )

        navigateTo(
            "${Screen.SmartTransferAmountScreen.baseRoute}/" +
                "${encodeData(smartAccount)}/$ibanAccount/" +
                "${SmartTransferTypes.SmartToIban.id}/${Screen.SmartTransferIbanAccountScreen.baseRoute}"
        )
    }

    private fun onShowAccountOptionsBottomSheet(selectedAccount: ACHAccount?) {
        this.selectedAccount = selectedAccount
        emitBaseEvent(OnShowAccountOptionsBottomSheet)
    }

    private fun onEditAccount() {
       uiState = uiState.copy(
           openDialog = DialogParameters(
               titleResource = R.string.smart_iban_transfer_edit_dialog_title,
               descriptionResource = R.string.smart_iban_transfer_edit_dialog_description,
               positiveResource = R.string.edit,
               negativeResource = R.string.exit,
               isActive = mutableStateOf(true),
               positiveAction = { navigateToEditNickname() }
           )
       )
    }

    private fun onDeleteAccount() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.smart_add_sac_account_dialog_delete_account_title,
                descriptionResource = R.string.smart_add_sac_account_dialog_delete_account_description,
                negativeResource = R.string.cancel,
                positiveResource = R.string.delete,
                positiveAction = {
                    callMutationACHTransferFavoriteDeleteUseCase()
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun callMutationACHTransferFavoriteDeleteUseCase() = selectedAccount?.let {
        executeUseCase {
            mutationACHTransferFavoriteDeleteUseCase.invoke(
                user,
                idBrand.toIntOrNull() ?: 0,
                it.accountForAchTransferId ?: 0
            ).collectLatest { result ->
                result.onSuccess {
                    onDeleteCardShowToast()
                    onCallListSinpeAccounts()
                }
                result.onFailure { onFailure(it) }
                result.onLoading { uiState = uiState.copy(isLoading = true) }
            }
        }
    }

    private fun onDeleteCardShowToast() {
        uiState = uiState.copy(
            toastIsVisible = true,
            toastMessage = R.string.smart_add_sac_account_toast_deleted_account
        )
    }

    private fun navigateToEditNickname() {
        navigateTo(
            Screen.SmartEditSavedIbanAccount.baseRoute
                .plus("/$user")
                .plus("/${idBrand.toIntOrNull()}")
                .plus("/$identification")
                .plus("/${selectedAccount?.accountForAchTransferId}")
        )
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var sinpeAccountList: List<ACHAccount?> = listOf(),
        val toastIsVisible: Boolean = false,
        val toastMessage: Int = R.string.empty
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is OnAddAccountClick -> navigateToAddIbanAccount()
            is OnNavigateBack -> onNavigateBack()
            is OnAccountClick -> onAccountClick(uiEvent.account)
            is OnCallListSinpeAccounts -> onCallListSinpeAccounts()
            is OnShowAccountOptions -> onShowAccountOptionsBottomSheet(uiEvent.account)
            is OnHideAccountOptions -> emitBaseEvent(OnHideAccountOptionsBottomSheet)
            is OnEditAccount -> onEditAccount()
            is OnDeleteAccount -> onDeleteAccount()
            is OnHideToast -> uiState = uiState.copy(
                toastIsVisible = false
            )
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnAddAccountClick : UIEvent()
        object OnCallListSinpeAccounts : UIEvent()
        data class OnAccountClick(val account: ACHAccount?) : UIEvent()
        data class OnShowAccountOptions(val account: ACHAccount?) : UIEvent()
        object OnHideAccountOptions : UIEvent()
        object OnEditAccount : UIEvent()
        object OnDeleteAccount : UIEvent()
        object OnHideToast : UIEvent()
    }

    sealed class BaseEvent {
        object OnShowAccountOptionsBottomSheet : BaseEvent()
        object OnHideAccountOptionsBottomSheet : BaseEvent()
    }
}
