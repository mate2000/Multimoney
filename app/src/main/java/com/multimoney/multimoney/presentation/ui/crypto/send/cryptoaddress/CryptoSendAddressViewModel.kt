package com.multimoney.multimoney.presentation.ui.crypto.send.cryptoaddress

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.crypto.ValidateDepositAddressUseCase
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.crypto.CryptoProcessErrorCodes
import com.multimoney.multimoney.presentation.util.CryptoConstants.BTC
import com.multimoney.multimoney.presentation.util.catalog.CheckboxDialogParameters
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoSendAddressViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val validateDepositAddressUseCase: ValidateDepositAddressUseCase,
) : BaseViewModel(shouldObserveToken = true) {

    // UIState
    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    private var notShowAgainVerifyCryptoAddress: Boolean = false
    var openMaintenanceAction = {}

    private fun onGetUserInfo(
        user: String?,
        idBrand: Int?,
        identification: String?,
        market: String
    ) {
        uiState = uiState.copy(
            user = user ?: "",
            idBrand = idBrand ?: 0,
            identification = identification ?: "",
            asset = market,
            showTextInputError = false
        )
    }

    private fun onContinueButtonClicked(onNextStep: () -> Unit) {
        if (!notShowAgainVerifyCryptoAddress) {
            uiState = uiState.copy(
                continueDialog = CheckboxDialogParameters(
                    titleResource = R.string.crypto_send_address_verify_crypto_address_title,
                    descriptionResource = R.string.crypto_send_address_verify_crypto_address_description,
                    positiveResource = R.string.button_continue,
                    negativeResource = R.string.common_go_back,
                    positiveAction = { isChecked ->
                        if (isChecked) {
                            setNotShowAgainVerifyCryptoAddress()
                        }
                        validateCryptoAddress(onNextStep)
                    },
                    isActive = mutableStateOf(true),
                    isCancelable = false
                )
            )
        } else {
            validateCryptoAddress(onNextStep)
        }
    }

    private fun validateCryptoAddress(onNextStep: () -> Unit) {
        // to send market we need to concatenate asset and BTC
        val market = "${uiState.asset}$BTC"
        executeUseCase {
            validateDepositAddressUseCase.invoke(
                user = uiState.user ?: "",
                identification = uiState.identification ?: "",
                idBrand = uiState.idBrand ?: 0,
                market = market,
                address = uiState.cryptoAddress.value
            ).collectLatest { result ->
                result.onSuccess { validateDepositAddress ->
                    if (validateDepositAddress.result != null) {
                        onNextStep()
                    } else {
                        uiState = uiState.copy(showTextInputError = true)
                    }
                    uiState = uiState.copy(isLoading = false)
                }
                result.onFailure {
                    if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                        openMaintenanceAction()
                        return@onFailure
                    }
                    onFailure(it)
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true, showTextInputError = false)
                }
            }
        }
    }

    private fun onFailure(error: HttpError) {
        uiState = uiState.copy(
            isLoading = false,
            showTextInputError = false,
            openDialog = DialogParameters(
                description = error.getError() ?: "",
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun setNotShowAgainVerifyCryptoAddress() {
        viewModelScope.launch {
            dataStorePreferences.setNotShowAgainVerifyCryptoAddress()
            notShowAgainVerifyCryptoAddress = true
        }
    }

    private fun onGetQrCodeFromSavedState(qrCodeResult: String) {
        uiState = uiState.copy(cryptoAddress = mutableStateOf(qrCodeResult))
        if (qrCodeResult.isNotBlank()) {
            validateCryptoAddress {}
        }
    }

    private fun onCryptoAddressChanged(cryptoAddress: String) {
        uiState = uiState.copy(
            cryptoAddress = mutableStateOf(cryptoAddress),
            showTextInputError = false
        )
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnContinueButtonClicked -> onContinueButtonClicked(event.onNextStep)
            is UIEvent.OnSetQrCodeFromSavedState -> onGetQrCodeFromSavedState(event.qrCodeResult)
            is UIEvent.GetNotShowAgainCryptoAddressFromSharedPref -> getNotShowAgainCryptoAddressFromSharedPref()
            is UIEvent.OnGetInfo -> onGetUserInfo(
                event.user,
                event.idBrand,
                event.identification,
                event.market
            )
            is UIEvent.OnCryptoAddressChanged -> onCryptoAddressChanged(event.cryptoAddress)
            is UIEvent.OnValidateCryptoAddress -> validateCryptoAddress() {}
            is UIEvent.OnSetOpenMaintenanceAction -> openMaintenanceAction = event.action
        }
    }

    private fun getNotShowAgainCryptoAddressFromSharedPref() {
        viewModelScope.launch {
            notShowAgainVerifyCryptoAddress =
                dataStorePreferences.getNotShowAgainVerifyCryptoAddress().first()
        }
    }

    data class UIState(
        val user: String? = null,
        val idBrand: Int? = null,
        val identification: String? = null,
        val asset: String? = null,
        val cryptoAddress: MutableState<String> = mutableStateOf(""),
        val continueDialog: CheckboxDialogParameters = CheckboxDialogParameters(),
        val notShowAgainVerifyCryptoAddress: Boolean = false,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val showTextInputError: Boolean = false,
        val onNextStep: () -> Unit = {}
    )

    sealed interface UIEvent {
        data class OnContinueButtonClicked(val onNextStep: () -> Unit) : UIEvent
        data class OnSetQrCodeFromSavedState(val qrCodeResult: String) : UIEvent
        data class OnGetInfo(
            val user: String,
            val idBrand: Int,
            val identification: String,
            val market: String
        ) : UIEvent

        data class OnCryptoAddressChanged(val cryptoAddress: String) : UIEvent
        data class OnValidateCryptoAddress(val cryptoAddress: String) : UIEvent
        object GetNotShowAgainCryptoAddressFromSharedPref : UIEvent
        data class OnSetOpenMaintenanceAction(val action: () -> Unit) : UIEvent
    }

    companion object {
        const val TEXT_DEBOUNCE_TIME = 1000L
    }
}
