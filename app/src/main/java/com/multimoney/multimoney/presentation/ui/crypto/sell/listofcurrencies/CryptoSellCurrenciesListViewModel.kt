package com.multimoney.multimoney.presentation.ui.crypto.sell.listofcurrencies

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CryptoSellCurrenciesListViewModel @Inject constructor() : BaseViewModel(true) {

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

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetUserInfo -> onGetUserInfo(
                event.user,
                event.idBrand,
                event.identification
            )
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
        data class OnGetUserInfo(
            val user: String?,
            val idBrand: Int?,
            val identification: String?
        ) : UIEvent
        object OnNavigateBack : UIEvent
    }
}
