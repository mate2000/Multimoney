package com.multimoney.multimoney.presentation.ui.crypto.send.cryptoamount

import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.crypto.GetTransferCommissionUseCase
import com.multimoney.domain.model.crypto.GetTransferFeeData
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.ui.crypto.purchase.buycurrency.BuyCurrencyScreenViewModel
import com.multimoney.multimoney.presentation.util.calculateAmountPlusFee
import com.multimoney.multimoney.presentation.util.calculateAssetEstimated
import com.multimoney.multimoney.presentation.util.calculateDollarEstimated
import com.multimoney.multimoney.presentation.util.catalog.CheckboxDialogParameters
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

@HiltViewModel
class CryptoSendAmountViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTransferCommissionUseCase: GetTransferCommissionUseCase,
) : BaseViewModel(shouldObserveToken = true) {

    var uiState by mutableStateOf(UIState())
        private set

    //share properties
    private var pkUser = 0
    private var asset = ""
    private var cryptoNetWork = ""
    private var idBrand = BuyCurrencyScreenViewModel.ID_BRAND_ERROR
    private var user = ""
    private var market = ""
    private var identification = ""
    private var assetImageUrl = ""
    private var destinationAddress = ""
    private var transferCommission: GetTransferFeeData? = null
    private var currentBalanceInDollar: Double = 0.0
    private var currentCryptoBalance: Double = 0.0
    private var currencyPrice: Double = 0.0

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
        currencyPrice: Double
    ) {
        this.pkUser = pkUser
        this.asset = asset ?: ""
        this.cryptoNetWork = cryptoNetwork ?: ""
        this.idBrand = idBrand
        this.user = user
        this.market = market ?: ""
        this.identification = identification
        this.assetImageUrl = assetImageUrl ?: ""
        this.destinationAddress = destinationAddress
        this.currentBalanceInDollar = currentBalanceInDollar
        this.currentCryptoBalance = currentCryptoBalance
        this.currencyPrice = currencyPrice
    }

    private fun onValidateAmountInput(amount: String) = executeUseCase {
        getTransferCommissionUseCase.invoke(
            user,
            idBrand,
            destinationAddress,
            asset,
            cryptoNetWork,
            amount.ifEmpty {
                DEFAULT_BASE_AMOUNT_STRING
            }.toDouble(),
        ).collectLatest { result ->
            result.onSuccess {
                transferCommission = it
                validateAmount(amount)
            }
            result.onFailure {
                uiState = uiState.copy(
                    isError = true
                )
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true, showTextInputError = false)
            }
        }
    }

    private fun validateAmount(amount: String) {

        val isCryptoCurrency = uiState.isTransformationCurrency.value

        val amountPlusFee = calculateAmountPlusFee(
            amount = if (isCryptoCurrency) amount else calculateAssetEstimated(
                amount,
                currencyPrice
            ),
            fee = transferCommission?.transferFee?.totalFee
        )

        when {
            uiState.quoteAmount.value.isEmpty() -> isError(isError = true)
            amountPlusFee >= currentCryptoBalance -> isError(
                errorMessage = R.string.crypto_purchase_flow_error_available_amount_commission,
                isError = true
            )
            else -> isError()
        }


    }

    private fun isError(
        @StringRes errorMessage: Int = R.string.empty, arg: Any = Any(), isError: Boolean = false
    ) {
        uiState = uiState.copy(
            error = errorMessage, errorMessageArg = arg, isError = isError
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
                currencyPrice = uiEvent.currencyPrice
            )
            is UIEvent.ValidateAmountInput -> onValidateAmountInput(uiEvent.amount)
            else -> {}
        }
    }

    data class UIState(
        val user: String? = null,
        val idBrand: Int? = null,
        val identification: String? = null,
        val asset: String? = null,
        val assetImageUrl: String? = null,
        val cryptoAddress: MutableState<String> = mutableStateOf(""),
        val quoteAmount: MutableState<String> = mutableStateOf(""),
        val baseAmount: MutableState<String> = mutableStateOf(""),
        val continueDialog: CheckboxDialogParameters = CheckboxDialogParameters(),
        val notShowAgainVerifyCryptoAddress: Boolean = false,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val showTextInputError: Boolean = false,
        val isError: Boolean = false,
        @StringRes val error: Int = R.string.empty,
        val errorMessageArg: Any = Any(),
        val isTransformationCurrency: MutableState<Boolean> = mutableStateOf(true),
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
            val currencyPrice: Double = 0.0
        ) : UIEvent()

        data class ValidateAmountInput(val amount: String) : UIEvent()
        data class OnSetFailureAction(val failureAction: () -> Unit) : UIEvent()
        object OnPurchaseCryptoCurrency : UIEvent()
        object OnGetExchangeRate : UIEvent()
        object OnOpenPurchaseConfirmationBottomSheet : UIEvent()
        object OnClosePurchaseConfirmationBottomSheet : UIEvent()
        object OnClearInputData : UIEvent()
    }

    companion object {
        const val DEFAULT_BASE_AMOUNT_STRING = "0.0"
    }
}