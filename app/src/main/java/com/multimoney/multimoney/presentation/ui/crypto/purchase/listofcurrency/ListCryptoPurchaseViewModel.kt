package com.multimoney.multimoney.presentation.ui.crypto.purchase.listofcurrency

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.crypto.GetAvailableListOfCryptoCoinsUseCase
import com.multimoney.domain.model.crypto.GetListOfAvailableCryptoCoins
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.util.CryptoHelper
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class ListCryptoPurchaseViewModel @Inject constructor(
    private val getAvailableListOfCryptoCoinsUseCase: GetAvailableListOfCryptoCoinsUseCase,
    private val dataStorePreferences: DataStorePreferences,
    private val cryptoHelper: CryptoHelper
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UiState())
        private set

    private fun onGetUserInfo(
        user: String?,
        idBrand: Int?
    ) {
        uiState = uiState.copy(
            user = user ?: "",
            idBrand = idBrand ?: 0,
        )
        viewModelScope.launch {
            uiState = uiState.copy(shouldDisplayDisclaimer = dataStorePreferences.isVolatileDialogVisible().first())
        }
    }

    private fun getAvailableListOfCryptoCoins() {
        executeUseCase {
            val cryptoOrigin = cryptoHelper.getCryptoOrigin()
            getAvailableListOfCryptoCoinsUseCase.invoke(
                uiState.user ?: "",
                uiState.idBrand ?: 0,
                cryptoOrigin
            )
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

    private fun onDisclaimerChecked(checked: Boolean) {
        uiState = uiState.copy(dontShowAgainChecked = checked)
    }
    private fun updateShouldShowDisclaimer(value: Boolean) {
        viewModelScope.launch {
            dataStorePreferences.setVolatileDialogVisible(!value)
            uiState = uiState.copy(shouldDisplayDisclaimer = preferences.isVolatileDialogVisible().first())
        }
    }

    data class UiState(
        val user: String? = null,
        val idBrand: Int? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val availableCryptoCoins: GetListOfAvailableCryptoCoins? = null,
        val shouldDisplayDisclaimer: Boolean = true,
        val dontShowAgainChecked: Boolean = false,
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
            is UIEvent.OnGetUserInfo -> onGetUserInfo(event.user, event.idBrand)
            is UIEvent.OnGetAvailableListOfCryptoCoins -> getAvailableListOfCryptoCoins()
            is UIEvent.OnDisclaimerChecked -> onDisclaimerChecked(event.checked)
            is UIEvent.OnUpdateShouldShowDisclaimer -> updateShouldShowDisclaimer(event.checked)
        }
    }

    sealed class UIEvent {
        data class OnGetUserInfo(val user: String?, val idBrand: Int?) : UIEvent()
        object OnNavigateBack : UIEvent()
        object OnGetAvailableListOfCryptoCoins : UIEvent()
        data class OnDisclaimerChecked(val checked: Boolean) : UIEvent()
        data class OnUpdateShouldShowDisclaimer(val checked: Boolean) : UIEvent()
    }

}
