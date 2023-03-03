package com.multimoney.multimoney.presentation.ui.crypto.send.cryptoamount

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.multimoney.data.util.catalog.TransferStatus
import com.multimoney.domain.interaction.crypto.GetTransferCommissionUseCase
import com.multimoney.domain.interaction.crypto.SendCryptoToAddressUseCase
import com.multimoney.domain.model.crypto.GetTransferFeeData
import com.multimoney.domain.model.crypto.SendCryptoToAddressData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.crypto.CryptoProcessErrorCodes
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.BuyCurrencyScreenViewModel
import com.multimoney.multimoney.presentation.util.calculateAmountPlusFee
import com.multimoney.multimoney.presentation.util.calculateAssetEstimated
import com.multimoney.multimoney.presentation.util.calculateDollarEstimated
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CryptoSendAmountViewModel @Inject constructor(
    private val getTransferCommissionUseCase: GetTransferCommissionUseCase,
    private val sendCryptoToAddressUseCase: SendCryptoToAddressUseCase
) : BaseViewModel(shouldObserveToken = true) {

    var uiState by mutableStateOf(UIState())
        private set

    //share properties
    private var pkUser = 0
    private var cryptoNetWork = ""
    private var idBrand = BuyCurrencyScreenViewModel.ID_BRAND_ERROR
    private var user = ""
    private var identification = ""
    private var assetImageUrl = ""
    private var currentBalanceInDollar: Double = 0.0
    private var currentCryptoBalance: Double = 0.0
    private var currencyPrice: Double = 0.0
    var asset = ""
    var destinationAddress = ""
    var openMaintenanceAction = {}

    private fun onSetUserData(
        pkUser: Int,
        asset: String?,
        cryptoNetwork: String?,
        idBrand: Int,
        user: String,
        identification: String,
        assetImageUrl: String?,
        destinationAddress: String,
        currentBalanceInDollar: Double,
        currentCryptoBalance: Double,
        currencyPrice: Double,
        openMaintenanceAction: () -> Unit
    ) {
        this.openMaintenanceAction = openMaintenanceAction
        this.pkUser = pkUser
        this.asset = asset ?: ""
        this.cryptoNetWork = cryptoNetwork ?: ""
        this.idBrand = idBrand
        this.user = user
        this.identification = identification
        this.assetImageUrl = assetImageUrl ?: ""
        this.destinationAddress = destinationAddress
        this.currentBalanceInDollar = currentBalanceInDollar
        this.currentCryptoBalance = currentCryptoBalance
        this.currencyPrice = currencyPrice
    }

    private fun onAmountChange(amount: String) {
        uiState = if (uiState.isTransformationCurrency.value) {
            uiState.copy(
                sendCryptoAmount = amount.ifEmpty { DEFAULT_BASE_AMOUNT_STRING }.toDouble(),
                sendDollarAmount = calculateDollarEstimated(
                    amount.ifEmpty { DEFAULT_BASE_AMOUNT_STRING },
                    currencyPrice
                ),
                feeCalculated = false
            )
        } else {
            uiState.copy(
                sendCryptoAmount = calculateAssetEstimated(
                    amount.ifEmpty { DEFAULT_BASE_AMOUNT_STRING },
                    currencyPrice
                ).toDouble(),
                sendDollarAmount = "$${amount.ifEmpty { DEFAULT_BASE_AMOUNT_STRING }}",
                feeCalculated = false
            )
        }
        isError()
    }

    private fun onCalculateAmountTransferCommission() = executeUseCase {
        getTransferCommissionUseCase.invoke(
            user,
            idBrand,
            destinationAddress,
            asset,
            cryptoNetWork,
            uiState.sendCryptoAmount,
        ).collectLatest { result ->
            result.onSuccess {
                uiState = uiState.copy(
                    transferCommission = it,
                    feeCalculated = true,
                    isLoading = false
                )
                validateAmountPlusFee()
            }
            result.onFailure {
                if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                    openMaintenanceAction()
                    return@onFailure
                }
                uiState = uiState.copy(
                    isError = true,
                    isLoading = false
                )
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true, showTextInputError = false)
            }
        }
    }

    private fun validateAmountPlusFee() {
        val amountPlusFee = calculateAmountPlusFee(
            amount = uiState.sendCryptoAmount.toString(),
            fee = uiState.transferCommission?.transferFee?.totalFee
        )
        when {
            amountPlusFee >= currentCryptoBalance -> isError(
                errorMessage = R.string.crypto_send_amount_error_available_amount_commission,
                isError = true
            )
            else -> isError()
        }
    }

    private fun isError(
        @StringRes errorMessage: Int = R.string.empty,
        arg: Any = Any(),
        isError: Boolean = false
    ) {
        uiState = uiState.copy(
            error = errorMessage,
            errorMessageArg = arg,
            isError = isError
        )
    }

    private fun sendCryptoToAddress() = executeUseCase {
        sendCryptoToAddressUseCase.invoke(
            pkUser = pkUser,
            identification = identification,
            destinationAddress = destinationAddress,
            feeId = uiState.transferCommission?.transferFee?.id ?: "",
            asset = asset,
            market = "$asset$USD_CURRENCY",
            cryptoNetwork = cryptoNetWork,
            amount = uiState.sendCryptoAmount,
            fee = uiState.transferCommission?.transferFee?.totalFee ?: FEE_DEFAULT_VALUE,
            internalFee = uiState.transferCommission?.transferFee?.internalFee ?: FEE_DEFAULT_VALUE,
            taxAmount = uiState.transferCommission?.transferFee?.taxAmount ?: TAX_DEFAULT_VALUE,
            idBrand = idBrand,
            user = user
        ).collectLatest { result ->
            result.onLoading {
                uiState = uiState.copy(
                    isLoading = true,
                    transferStatus = TransferStatus.LOADING,
                )
            }
            result.onSuccess {
                uiState = uiState.copy(
                    isLoading = false,
                    transferStatus = TransferStatus.SUCCESS,
                    cryptoSendAmountData = it,
                    referenceNumber = it.transferOrder.result?.sysdeTransactionNumber ?: ""
                )
            }
            result.onFailure {
                if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                    openMaintenanceAction()
                    return@onFailure
                }
                uiState = if (uiState.failed.not()) {
                    uiState.copy(
                        failed = true,
                        isLoading = false,
                        transferStatus = TransferStatus.FAILED
                    )
                } else {
                    uiState.copy(
                        isLoading = false,
                        transferStatus = TransferStatus.ERROR
                    )
                }
            }
        }
    }

    private fun clearInputData() {
        uiState = uiState.copy(
            sendCryptoAmount = 0.0,
            sendDollarAmount = "",
            baseAmount = mutableStateOf(""),
            quoteAmount = mutableStateOf(""),
            isError = false
        )
    }

    fun onUIEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.OnSetUserData -> onSetUserData(
                pkUser = uiEvent.pkUser,
                asset = uiEvent.asset,
                cryptoNetwork = uiEvent.cryptoNetwork,
                idBrand = uiEvent.idBrand,
                user = uiEvent.user,
                identification = uiEvent.identification,
                assetImageUrl = uiEvent.assetImageUrl,
                destinationAddress = uiEvent.destinationAddress,
                currentBalanceInDollar = uiEvent.currentBalanceInDollar,
                currentCryptoBalance = uiEvent.currentCryptoBalance,
                currencyPrice = uiEvent.currencyPrice,
                openMaintenanceAction = uiEvent.openMaintenanceAction
            )
            is UIEvent.OnAmountChanged -> onAmountChange(uiEvent.amount)
            is UIEvent.OnCalculateAmountTransferCommission -> onCalculateAmountTransferCommission()
            is UIEvent.OnSendCryptoCurrency -> sendCryptoToAddress()
            is UIEvent.OnClearInputData -> clearInputData()
        }
    }

    data class UIState(
        val user: String? = null,
        val idBrand: Int? = null,
        val identification: String? = null,
        val asset: String? = null,
        val assetImageUrl: String? = null,
        val quoteAmount: MutableState<String> = mutableStateOf(""),
        val baseAmount: MutableState<String> = mutableStateOf(""),
        val sendCryptoAmount: Double = 0.0,
        val sendDollarAmount: String = "",
        val isLoading: Boolean = false,
        val showTextInputError: Boolean = false,
        val isError: Boolean = false,
        @StringRes val error: Int = R.string.empty,
        val errorMessageArg: Any = Any(),
        val isTransformationCurrency: MutableState<Boolean> = mutableStateOf(true),
        val transferStatus: TransferStatus = TransferStatus.IDLE,
        val transferCommission: GetTransferFeeData? = null,
        val feeCalculated: Boolean = false,
        val referenceNumber: String = "",
        val cryptoSendAmountData: SendCryptoToAddressData? = null,
        val failed: Boolean = false,
        val failedFirstTime: Boolean = false,
    )

    sealed class UIEvent {
        data class OnSetUserData(
            val pkUser: Int,
            val asset: String,
            val cryptoNetwork: String?,
            val idBrand: Int,
            val user: String,
            val identification: String,
            val destinationAddress: String,
            val assetImageUrl: String?,
            val currentBalanceInDollar: Double = 0.0,
            val currentCryptoBalance: Double = 0.0,
            val currencyPrice: Double = 0.0,
            val openMaintenanceAction: () -> Unit = {}
        ) : UIEvent()

        data class OnAmountChanged(val amount: String) : UIEvent()
        object OnCalculateAmountTransferCommission : UIEvent()
        object OnSendCryptoCurrency : UIEvent()
        object OnClearInputData : UIEvent()
    }

    companion object {
        const val MINIMUM_SEND_AMOUNT = 0.0
        const val DEFAULT_BASE_AMOUNT_STRING = "0.0"
        const val USD_CURRENCY = "USD"
        const val FEE_DEFAULT_VALUE = 0.0
        const val TAX_DEFAULT_VALUE = 0.0
        const val TEXT_DEBOUNCE_TIME = 500L
    }
}