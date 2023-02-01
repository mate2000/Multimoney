package com.multimoney.multimoney.presentation.ui.crypto.send.listofcurrencies

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.crypto.GetBalanceCryptoAccountUseCase
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CryptoSendCurrenciesListViewModel @Inject constructor(
    private val getBalanceCryptoAccountUseCase: GetBalanceCryptoAccountUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UiState())
        private set

    private fun onGetUserInfo(
        user: String?,
        idBrand: Int?,
        identification: String?
    ) {
        uiState = uiState.copy(
            user = user ?: "",
            idBrand = idBrand ?: 0,
            identification = identification ?: ""
        )
    }

    private fun getBalanceCrypto() {
        executeUseCase {
            getBalanceCryptoAccountUseCase.invoke(
                user = uiState.user ?: "",
                identification = uiState.identification ?: "",
                idBrand = uiState.idBrand ?: 0,
            ).collectLatest { result ->
                result.onSuccess { balance ->
                    uiState = uiState.copy(
                        isLoading = false,
                        cryptoAccounts = balance.items ?: emptyList()
                    )
                }
                result.onFailure {
                    onFailure(it)
                }
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

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetUserInfo -> onGetUserInfo(event.user, event.idBrand, event.identification)
            is UIEvent.OnGetBalanceCrypto -> getBalanceCrypto()
        }
    }

    data class UiState(
        val user: String? = null,
        val idBrand: Int? = null,
        val identification: String? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val cryptoAccounts: List<BalanceCryptoAccountItems> = listOf(),
    )

    sealed interface UIEvent {
        data class OnGetUserInfo(val user: String?, val idBrand: Int?, val identification: String?) : UIEvent
        object OnNavigateBack : UIEvent
        object OnGetBalanceCrypto : UIEvent
    }
}