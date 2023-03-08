package com.multimoney.multimoney.presentation.ui.crypto.receive.cryptoaddress

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.domain.interaction.crypto.GetCryptoReceiveAddressUseCase
import com.multimoney.domain.interaction.crypto.ValidateDepositAddressUseCase
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.crypto.CryptoProcessErrorCodes
import com.multimoney.multimoney.presentation.util.ShareHelper
import com.multimoney.multimoney.presentation.util.catalog.AdjustEventType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CryptoReceiveAddressViewModel @Inject constructor(
    private val helper: ShareHelper,
    private val validateDepositAddressUseCase: ValidateDepositAddressUseCase,
    private val getCryptoReceiveAddressUseCase: GetCryptoReceiveAddressUseCase
) : BaseViewModel(shouldObserveToken = true) {

    var uiState by mutableStateOf(UIState())
        private set

    //maintenance event
    var openMaintenanceAction = {}

    private fun onGetUserInfo(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        cryptoNetwork: String
    ) {
        uiState = uiState.copy(
            user = user,
            idBrand = idBrand,
            identification = identification,
            asset = market,
            cryptoNetwork = cryptoNetwork
        )
    }

    private fun getCryptoReceiveAddress() {
        executeUseCase {
            getCryptoReceiveAddressUseCase.invoke(
                user = uiState.user,
                idBrand = uiState.idBrand,
                identification = uiState.identification,
                asset = uiState.asset,
                crypto_network = uiState.cryptoNetwork
            ).collectLatest { result ->
                result.onSuccess { data ->
                    uiState = uiState.copy(
                        isLoading = false,
                        address = data.cryptoReceiveAddress.address
                    )
                }
                result.onFailure {
                    if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                        openMaintenanceAction()
                        return@onFailure
                    }
                    onFailure(it)
                }
                result.onLoading {
                    uiState = uiState.copy(isLoading = true)
                }
            }
        }
    }

    private fun shareCryptoReceiveAddress(address: String, bitmap: Bitmap) {
        helper.shareTextWithImage(text = address, bitmap = bitmap)
    }

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnGetInfo -> onGetUserInfo(
                event.user,
                event.idBrand,
                event.identification,
                event.market,
                event.cryptoNetwork
            )
            is UIEvent.OnGetCryptoReceiveAddress -> getCryptoReceiveAddress()
            is UIEvent.OnShareCryptoReceiveAddress -> shareCryptoReceiveAddress(event.address, event.bitmap)
            is UIEvent.OnSetOpenMaintenanceAction -> openMaintenanceAction = event.action
            UIEvent.OnRegisterAdjustEnterReceiveQRScreen -> registerAdjustEvent(
                applyAdjust = false,
                adjustEventType = AdjustEventType.RECEIVE_CRYPTO_ENTER_QR_SCREEN
            )
            UIEvent.OnRegisterAdjustPressShareAddressButton -> registerAdjustEvent(
                applyAdjust = false,
                adjustEventType = AdjustEventType.RECEIVE_CRYPTO_PRESS_SHARE_BUTTON
            )
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

    data class UIState(
        val user: String = "",
        val idBrand: Int = 0,
        val identification: String = "",
        val asset: String = "",
        val cryptoNetwork: String = "",
        val isLoading: Boolean = true,
        val address: String = "",
        val openDialog: DialogParameters = DialogParameters(),
    )

    sealed interface UIEvent {
        data class OnGetInfo(
            val user: String,
            val idBrand: Int,
            val identification: String,
            val market: String,
            val cryptoNetwork: String,
        ) : UIEvent
        object OnGetCryptoReceiveAddress : UIEvent
        data class OnShareCryptoReceiveAddress(
            val address: String,
            val bitmap: Bitmap
        ) : UIEvent
        data class OnSetOpenMaintenanceAction(val action: () -> Unit) : UIEvent
        object OnRegisterAdjustEnterReceiveQRScreen : UIEvent
        object OnRegisterAdjustPressShareAddressButton : UIEvent
    }
}