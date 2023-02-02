package com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.accountsmart.QueryACHTransferFavoriteListUseCase
import com.multimoney.domain.model.accountsmart.ACHFavoriteAccount
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnCallQueryACHTransferFavoriteListUseCase
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnFavoriteClick
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnNavigateBack
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnNavigateToHome
import com.multimoney.multimoney.presentation.ui.smart.transfer.smart.favorite.account.SmartTransferFavoriteViewModel.UIEvent.OnOptionsClick
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class SmartTransferFavoriteViewModel @Inject constructor(
    private val queryACHTransferFavoriteListUseCase: QueryACHTransferFavoriteListUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // Stateless
    private var user: String = ""
    private var idBrand: String = ""
    private var identification: String? = ""

    init {
        user = savedStateHandle[USER] ?: ""
        idBrand = savedStateHandle[ID_BRAND] ?: ""
        identification = savedStateHandle[IDENTIFICATION] ?: ""
    }

    private fun callQueryACHTransferFavoriteListUseCase() = executeUseCase {
        queryACHTransferFavoriteListUseCase.invoke(
            user = user,
            identification = identification ?: "",
            idBrand = idBrand.toInt(),
            isFavorite = true
        ).collectLatest { result ->
            result.onSuccess { ACHFavoriteAccountList ->
                uiState = uiState.copy(isLoading = false)
                ACHFavoriteAccountList?.data?.let { ACHFavoriteAccounts ->
                    uiState =
                        uiState.copy(
                            ACHFavoriteAccountList = ACHFavoriteAccounts.sortedBy { ACHFavoriteAccount ->
                                ACHFavoriteAccount?.description.orEmpty()
                            }.groupBy { ACHFavoriteAccount ->
                                ACHFavoriteAccount?.accountNumber.orEmpty()
                            })
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

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(
            isLoading = false, openDialog = DialogParameters(
                description = error.getError() ?: "", isActive = mutableStateOf(true)
            )
        )
    }

    private fun onNavigateBack() = navigateBack(
        popTo = Screen.SmartSelectSendingTypeScreen.route, isRestart = false
    )

    private fun onShowOptionsClick(selectedACHFavorite:ACHFavoriteAccount?) {
      // TODO REV-3466
    }

    private fun onACHFavoriteClick(selectedACHFavoriteAccount: List<ACHFavoriteAccount?>) {
//      TODO REV-3654
    }

    private fun onNavigateToHome() {
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.UNEXPANDED
        )
    }

    data class UIState(
        val openDialog: DialogParameters = DialogParameters(),
        var isLoading: Boolean = false,
        var ACHFavoriteAccountList: Map<String, List<ACHFavoriteAccount?>> = mapOf(),
    )

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
             is OnOptionsClick -> onShowOptionsClick(uiEvent.ACHFavorite)
            is OnNavigateBack -> onNavigateBack()
            is OnFavoriteClick -> onACHFavoriteClick(uiEvent.ACHFavorites)
            is OnNavigateToHome -> onNavigateToHome()
            OnCallQueryACHTransferFavoriteListUseCase -> callQueryACHTransferFavoriteListUseCase()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnNavigateToHome : UIEvent()
        data class OnOptionsClick(val ACHFavorite: ACHFavoriteAccount?) : UIEvent()
        object OnCallQueryACHTransferFavoriteListUseCase : UIEvent()
        data class OnFavoriteClick(val ACHFavorites: List<ACHFavoriteAccount?>) : UIEvent()
    }
}