@file:OptIn(ExperimentalMaterialApi::class)

package com.multimoney.multimoney.presentation.ui.home.profile.accounts

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.multimoney.multimoney.presentation.util.NavEvent
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MyAccountsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val queryListSinpeAccountUseCase: QueryListSinpeAccountUseCase,
    private val mutationManageSinpeAccountUpdate: MutationSinpeAccountUpdateUseCase

) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: Int = 0
    private var identification: String = ""

    init {
        user = savedStateHandle[USER_NAME] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: 0
        identification = savedStateHandle[IDENTIFICATION] ?: ""
        callListSinpeAccountUseCase()
        callListSinpeAccountUseCase(isFavorite = true)
    }

    private fun onUpdateAccount() = executeUseCase {
        mutationManageSinpeAccountUpdate.invoke(
            user = user,
            idBrand = idBrand,
            identification = identification,
            accountNumber = uiState.selectedAccount?.sinpeAccount ?: "",
            idCurrency = uiState.selectedAccount?.currencyId?.toLong() ?: 0,
            nameAccount = uiState.accountNickname ?: "",
            isFavorite = false,
            idBank = uiState.selectedAccount?.idBank ?: 0,
            typeAccount = uiState.selectedAccount?.typeAccount ?: 0,
            idAccount = uiState.selectedAccount?.accountId ?: 0
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(isLoading = true)
                onGoBackToMyAccounts()
                onShowSnackBar()
                uiState = uiState.copy(registeredAccounts = listOf(), favoriteAccounts = listOf())
                callListSinpeAccountUseCase(true)
                callListSinpeAccountUseCase(false)
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false)
//                uiState = uiState.copy(
//                    openDialog = DialogParameters(
//                        description = it.getError().toString(),
//                        isActive = mutableStateOf(true)
//                    )
//                )
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun callListSinpeAccountUseCase(isFavorite: Boolean = false) = executeUseCase {
        queryListSinpeAccountUseCase.invoke(
            user = user,
            identification = identification,
            idBrand = idBrand,
            country = "",
            idAccount = 0,
            accountNumber = "",
            isFavorite = isFavorite
        ).collectLatest { result ->
            result.onSuccess { accounts ->
                uiState = uiState.copy(isLoading = false)
                if (accounts != null) {
                    uiState = if (isFavorite) {
                        uiState.copy(favoriteAccounts = accounts.data)
                    } else
                        uiState.copy(registeredAccounts = accounts.data)
                }
            }.onFailure {
                uiState = uiState.copy(isLoading = false)
            }.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun onStart(snackBarTitle: String) {
        uiState = uiState.copy(snackBarTitle = snackBarTitle)
    }

    private fun toggleBottomSheet(value: ModalBottomSheetState) {
        uiState = uiState.copy(bottomSheetVisibleState = value)
    }

    private fun onAccountClicked(sinpeAccount: SinpeAccount) {
        uiState = uiState.copy(selectedAccount = sinpeAccount)
        toggleBottomSheet(ModalBottomSheetState(ModalBottomSheetValue.Expanded))
    }

    private fun onEditNickname() {
        toggleBottomSheet(ModalBottomSheetState(ModalBottomSheetValue.Hidden))
        uiState = uiState.copy(isEditing = true)
    }

    private fun onValueChanged(value: String) {
        uiState = uiState.copy(accountNickname = value)
    }

    private fun onGoBackToMyAccounts() {
        uiState = uiState.copy(isEditing = false)
    }

    private fun onShowSnackBar() {
        uiState = uiState.copy(showSnackBar = true)
    }

    private fun onDismissSnackBar() {
        uiState = uiState.copy(showSnackBar = false)
    }

    private fun onSetupNickNamePlaceholder(currentNickname: String) {
        uiState = uiState.copy(accountNickname = currentNickname)
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.ProfileScreen.route, false)
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
            UIEvent.OnAddToFavorite -> TODO()
            UIEvent.OnDeleteAccount -> TODO()
            UIEvent.OnEditNickname -> onEditNickname()
            UIEvent.OnRemoveFromFavorites -> TODO()
            UIEvent.OnUpdateAccount -> onUpdateAccount()
            is UIEvent.OnValueChanged -> onValueChanged(uiEvent.value)
            is UIEvent.OnShowSnackBar -> onShowSnackBar()
            is UIEvent.OnDismissSnackBar -> onDismissSnackBar()
            is UIEvent.OnStart -> onStart(uiEvent.snackBarTitle)
            is UIEvent.OnSetupNickNamePlaceholder -> onSetupNickNamePlaceholder(uiEvent.currentNickname)
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        data class OnAccountClicked(val account: SinpeAccount) : UIEvent()
        data class OnValueChanged(val value: String) : UIEvent()
        data class OnSetupNickNamePlaceholder(val currentNickname: String) : UIEvent()
        object OnShowBottomSheet : UIEvent()
        object OnHideBottomSheet : UIEvent()
        object OnDeleteAccount : UIEvent()
        object OnEditNickname : UIEvent()
        object OnAddToFavorite : UIEvent()
        object OnRemoveFromFavorites : UIEvent()
        object OnGoBackToMyAccounts : UIEvent()
        object OnUpdateAccount : UIEvent()
        object OnShowSnackBar : UIEvent()
        object OnDismissSnackBar : UIEvent()
        data class OnStart(val snackBarTitle: String) : UIEvent()
    }

    data class UIState(
        // Fields
        val favoriteAccounts: List<SinpeAccount?> = listOf(),
        val registeredAccounts: List<SinpeAccount?> = listOf(),
        val selectedAccount: SinpeAccount? = null,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden
        ),
        val accountNickname: String? = null,
        val isEditing: Boolean = false,
        val showSnackBar: Boolean = false,
        val snackBarTitle: String = "",
        val isLoading: Boolean = false
    )
}
