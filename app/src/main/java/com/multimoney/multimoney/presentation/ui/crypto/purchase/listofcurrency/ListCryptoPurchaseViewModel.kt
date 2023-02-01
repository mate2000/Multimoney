package com.multimoney.multimoney.presentation.ui.crypto.purchase.listofcurrency

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.crypto.GetAvailableListOfCryptoCoinsUseCase
import com.multimoney.domain.model.crypto.GetListOfAvailableCryptoCoins
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class ListCryptoPurchaseViewModel @Inject constructor(
    private val getAvailableListOfCryptoCoinsUseCase: GetAvailableListOfCryptoCoinsUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UiState())
        private set

    private fun onGetUserInfo(
        user: String?,
        idBrand: Int?
    ) {
        uiState = uiState.copy(
            user = user ?: "",
            idBrand = idBrand ?: 0
        )
    }

    private fun getAvailableListOfCryptoCoins() {
        executeUseCase {
            getAvailableListOfCryptoCoinsUseCase.invoke(uiState.user ?: "", uiState.idBrand ?: 0)
                .collectLatest { result ->
                    result.onSuccess { availableCryptoCoins ->
                        availableCryptoCoins.let {
                            uiState = uiState.copy(
                                isLoading = false,
                                availableCryptoCoins = it
                            )
                        }
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

    data class UiState(
        val user: String? = null,
        val idBrand: Int? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val availableCryptoCoins: GetListOfAvailableCryptoCoins? = null
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetUserInfo -> onGetUserInfo(event.user, event.idBrand)
            is UIEvent.OnGetAvailableListOfCryptoCoins -> getAvailableListOfCryptoCoins()
        }
    }

    sealed interface UIEvent {
        data class OnGetUserInfo(val user: String?, val idBrand: Int?) : UIEvent
        object OnNavigateBack : UIEvent
        object OnGetAvailableListOfCryptoCoins : UIEvent
    }
}