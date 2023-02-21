@file:OptIn(ExperimentalMaterialApi::class)

package com.multimoney.multimoney.presentation.ui.home.profile.accounts

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.MutationSinpeAccountDeleteUseCase
import com.multimoney.domain.interaction.accountsmart.MutationSinpeAccountUpdateUseCase
import com.multimoney.domain.interaction.accountsmart.QueryListSinpeAccountUseCase
import com.multimoney.domain.model.accountsmart.SinpeAccount
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_NAME
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MyAccountsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryListSinpeAccountUseCase: QueryListSinpeAccountUseCase,
    private val mutationManageSinpeAccountUpdate: MutationSinpeAccountUpdateUseCase,
    private val mutationManageSinpeAccountDelete: MutationSinpeAccountDeleteUseCase

) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var identification: String = ""

    init {
        user = savedStateHandle[USER_NAME] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        uiState = uiState.copy(idBrand = savedStateHandle[ID_BRAND] ?: 0)
    }

    private fun onStart() {
        callListSinpeAccountUseCase()
        callListSinpeAccountUseCase(isFavorite = true)
    }

    private fun callListSinpeAccountUseCase(isFavorite: Boolean = false) = executeUseCase {
        queryListSinpeAccountUseCase.invoke(
            user = user,
            identification = identification,
            idBrand = uiState.idBrand,
            country = "",
            idAccount = 0,
            accountNumber = "",
            isFavorite = isFavorite
        ).collectLatest { result ->
            result.onSuccess { accounts ->
                uiState = uiState.copy(isLoading = false)
                if (accounts != null) {
                    uiState = if (isFavorite) {
                        uiState.copy(favoriteAccounts = accounts.data, favoritesLoaded = true)
                    } else
                        uiState.copy(registeredAccounts = accounts.data, registeredLoaded = true)
                }
            }.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    showErrorDialog = true
                )
            }.onLoading {
                uiState = if (isFavorite) {
                    uiState.copy(favoritesLoaded = false)
                } else
                    uiState.copy(registeredLoaded = false)
            }
        }
    }

    private fun onUpdateAccount() = executeUseCase {
        mutationManageSinpeAccountUpdate.invoke(
            user = user,
            idBrand = uiState.idBrand,
            identification = identification,
            accountNumber = uiState.selectedAccount?.sinpeAccount ?: "",
            idCurrency = uiState.selectedAccount?.currencyId?.toLong() ?: 0,
            nameAccount = uiState.accountNickname ?: "",
            isFavorite = uiState.selectedAccount?.isFavorite ?: true,
            idBank = uiState.selectedAccount?.idBank ?: 0,
            typeAccount = uiState.selectedAccount?.typeAccount ?: 0,
            idAccount = uiState.selectedAccount?.accountId ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = true)
                onGoBackToMyAccounts()
                onShowSnackBar(message = R.string.profile_my_account_account_updated)
                uiState = uiState.copy(registeredAccounts = listOf(), favoriteAccounts = listOf())
                callListSinpeAccountUseCase(true)
                callListSinpeAccountUseCase(false)
            }
            result.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    showErrorDialog = true
                )
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }


    private fun onChangeFavorite() {
        if (uiState.selectedAccount?.isFavorite == true) {
            onOpenRemoveFavDialog()
        } else
            onToggleFavorite()
    }

    private fun onToggleFavorite() = executeUseCase {
        toggleBottomSheet(ModalBottomSheetState(ModalBottomSheetValue.Hidden))
        mutationManageSinpeAccountUpdate.invoke(
            user = user,
            idBrand = uiState.idBrand,
            identification = identification,
            accountNumber = uiState.selectedAccount?.sinpeAccount ?: "",
            idCurrency = uiState.selectedAccount?.currencyId?.toLong() ?: 0,
            nameAccount = uiState.selectedAccount?.nameAccount ?: "",
            isFavorite = uiState.selectedAccount?.isFavorite?.not() ?: true,
            idBank = uiState.selectedAccount?.idBank ?: 0,
            typeAccount = uiState.selectedAccount?.typeAccount ?: 0,
            idAccount = uiState.selectedAccount?.accountId ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                onGoBackToMyAccounts()
                onShowSnackBar(message = if (uiState.selectedAccount?.isFavorite == true) R.string.profile_my_accounts_removed_from_fav else R.string.profile_my_accounts_added_as_fav)
                refreshAccounts()
            }
            result.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    showErrorDialog = true
                )
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onDeleteAccount() = executeUseCase {
        toggleBottomSheet(ModalBottomSheetState(ModalBottomSheetValue.Hidden))
        mutationManageSinpeAccountDelete.invoke(
            user = user,
            idBrand = uiState.idBrand,
            identification = identification,
            idAccount = uiState.selectedAccount?.accountId ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                onShowSnackBar(message = R.string.profile_my_accounts_deleted)
                refreshAccounts()
            }
            result.onFailure {
                uiState = uiState.copy(
                    isLoading = false,
                    showErrorDialog = true
                )
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun toggleBottomSheet(value: ModalBottomSheetState) {
        uiState = uiState.copy(bottomSheetVisibleState = value)
    }

    private fun onAccountClicked(sinpeAccount: SinpeAccount) {
        uiState = uiState.copy(selectedAccount = sinpeAccount)
        uiState = if (sinpeAccount.isFavorite)
            uiState.copy(favoriteTextResource = R.string.profile_my_accounts_delete_as_fav)
        else
            uiState.copy(favoriteTextResource = R.string.profile_my_accounts_add_as_fav)
        toggleBottomSheet(ModalBottomSheetState(ModalBottomSheetValue.Expanded))
    }

    private fun onEditNickname() {
        toggleBottomSheet(ModalBottomSheetState(ModalBottomSheetValue.Hidden))
        uiState = uiState.copy(isEditing = true)
    }

    private fun onValueChanged(value: String) {
        uiState = uiState.copy(accountNickname = value)
        validateForm()
    }

    private fun validateForm() {
        uiState =
            if (uiState.accountNickname.isNullOrEmpty() || uiState.accountNickname.isNullOrBlank())
                uiState.copy(isButtonEnabled = false)
            else
                uiState.copy(isButtonEnabled = true)
    }

    private fun onGoBackToMyAccounts() {
        uiState = uiState.copy(isEditing = false)
    }

    private fun onShowSnackBar(message: Int) {
        uiState = uiState.copy(showSnackBar = true, snackBarTitleResource = message)
    }

    private fun onDismissSnackBar() {
        uiState = uiState.copy(showSnackBar = false)
    }

    private fun onSetupNickNamePlaceholder(currentNickname: String) {
        uiState = uiState.copy(accountNickname = currentNickname)
    }

    private fun onOpenRemoveFavDialog() {
        uiState = uiState.copy(
            favoriteDialogParemeters = DialogParameters(
                titleResource = R.string.profile_my_accounts_remove_from_fav,
                descriptionResource = R.string.profile_my_accounts_you_can_fav_later,
                positiveResource = R.string.button_continue,
                negativeResource = R.string.cancel,
                positiveAction = {
                    onToggleFavorite()
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onOpenDeleteAccountDialog() {
        uiState = uiState.copy(
            favoriteDialogParemeters = DialogParameters(
                titleResource = if(uiState.idBrand == Brand.CostaRica.id) R.string.profile_my_accounts_are_you_sure_to_delete else R.string.profile_my_accounts_are_you_sure_to_delete_gt,
                descriptionResource = R.string.profile_my_accounts_will_be_deleted_permanently,
                positiveResource = R.string.button_continue,
                negativeResource = R.string.cancel,
                positiveAction = {
                    onDeleteAccount()
                },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun refreshAccounts() {
        uiState = uiState.copy(
            registeredAccounts = listOf(),
            favoriteAccounts = listOf(),
            isLoading = true,
            registeredLoaded = false,
            favoritesLoaded = false
        )
        callListSinpeAccountUseCase(true)
        callListSinpeAccountUseCase(false)
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.ProfileScreen.route, false)
            is UIEvent.OnNavigateHome -> navigateBack(Screen.HomeScreen.route, true)
            is UIEvent.OnAccountClicked -> onAccountClicked(uiEvent.account)
            is UIEvent.OnShowBottomSheet -> toggleBottomSheet(
                ModalBottomSheetState(
                    ModalBottomSheetValue.Expanded
                )
            )
            is UIEvent.OnHideBottomSheet -> toggleBottomSheet(
                ModalBottomSheetState(
                    ModalBottomSheetValue.Hidden
                )
            )
            UIEvent.OnGoBackToMyAccounts -> onGoBackToMyAccounts()
            UIEvent.OnToggleFavorite -> onToggleFavorite()
            UIEvent.OnDeleteAccount -> onDeleteAccount()
            UIEvent.OnEditNickname -> onEditNickname()
            UIEvent.OnRemoveFromFavorites -> TODO()
            UIEvent.OnUpdateAccount -> onUpdateAccount()
            is UIEvent.OnValueChanged -> onValueChanged(uiEvent.value)
            is UIEvent.OnDismissSnackBar -> onDismissSnackBar()
            is UIEvent.OnSetupNickNamePlaceholder -> onSetupNickNamePlaceholder(uiEvent.currentNickname)
            is UIEvent.OnOpenDeleteDialog -> onOpenDeleteAccountDialog()
            is UIEvent.OnStart -> onStart()
            is UIEvent.OnChangeFavorite -> onChangeFavorite()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateHome : UIEvent()
        data class OnAccountClicked(val account: SinpeAccount) : UIEvent()
        data class OnValueChanged(val value: String) : UIEvent()
        data class OnSetupNickNamePlaceholder(val currentNickname: String) : UIEvent()
        object OnOpenDeleteDialog : UIEvent()
        object OnShowBottomSheet : UIEvent()
        object OnHideBottomSheet : UIEvent()
        object OnDeleteAccount : UIEvent()
        object OnEditNickname : UIEvent()
        object OnToggleFavorite : UIEvent()
        object OnRemoveFromFavorites : UIEvent()
        object OnGoBackToMyAccounts : UIEvent()
        object OnUpdateAccount : UIEvent()
        object OnDismissSnackBar : UIEvent()
        object OnChangeFavorite : UIEvent()
        object OnStart : UIEvent()
    }

    data class UIState(
        // Fields
        val idBrand: Int = 0,
        val favoriteAccounts: List<SinpeAccount?> = listOf(),
        val registeredAccounts: List<SinpeAccount?> = listOf(),
        val selectedAccount: SinpeAccount? = null,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden
        ),
        val accountNickname: String? = null,
        val isEditing: Boolean = false,
        val showSnackBar: Boolean = false,
        val snackBarTitleResource: Int = R.string.empty,
        val favoriteTextResource: Int = R.string.empty,
        val isLoading: Boolean = false,
        val favoritesLoaded: Boolean = false,
        val registeredLoaded: Boolean = false,
        val favoriteDialogParemeters: DialogParameters = DialogParameters(),
        val deleteDialogParameters: DialogParameters = DialogParameters(),
        val isButtonEnabled: Boolean = false,
        val showErrorDialog: Boolean = false

    )
}
