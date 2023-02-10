package com.multimoney.multimoney.presentation.ui.crypto.sell

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.PurchaseCryptoSteps
import com.multimoney.domain.model.accountsmart.SmartAccountSmall
import com.multimoney.domain.model.balance.BalanceCryptoAccountItems
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.SMART_ACCOUNTS_LIST
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.USER_CRYPTO_BALANCES
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_MARKET
import com.multimoney.multimoney.presentation.ui.crypto.CryptoOperationSide
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class SellCryptoSharedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    private var currentFlowStep: Int = PurchaseCryptoSteps.One.pageNumber

    //bundle parameters
    var idBrand = DEFAULT_ID_BRAND_ERROR
    var pkUser = ""
    var user = ""
    var identification = ""
    var email = ""
    private var marketCryptoCoin: MarketCryptoCoin? = savedStateHandle[ITEM_CRYPTO_MARKET]
    var abvCurrency: String = ""
    val side = CryptoOperationSide.SELL.value
    val comingFromDetails: Boolean = marketCryptoCoin != null

    private fun setUserData() {

        viewModelScope.launch {
            idBrand = dataStorePreferences.getIdBrand().first().toInt()
            pkUser = dataStorePreferences.getPkUser().first()
            user = dataStorePreferences.getUserName().first()
            identification = dataStorePreferences.getIdentification().first()
            email = dataStorePreferences.getUserEmail().first()
            abvCurrency = CurrencyType.Dollar.disbursementValue

            uiState = uiState.copy(
                asset = marketCryptoCoin?.baseAsset,
                assetDescription = marketCryptoCoin?.description,
                market = marketCryptoCoin?.baseAsset?.plus(abvCurrency),
                cryptoNetWork = marketCryptoCoin?.cryptoNetwork,
                assetImageBaseUrl = marketCryptoCoin?.url_image,
                shouldDisplayDisclaimer = preferences.isVolatileDialogVisible().first(),
            )
        }

        uiState = uiState.copy(
            accounts = savedStateHandle.get<Array<SmartAccountSmall>>(SMART_ACCOUNTS_LIST)?.toList()
                ?: listOf(),
            userCryptoBalances = savedStateHandle.get<Array<BalanceCryptoAccountItems>>(
                USER_CRYPTO_BALANCES
            )?.toList()
                ?: listOf()
        )
        if (idBrand == Brand.ElSalvador.id) {
            onSetupAccountDetails(
                smartAccountAvailableBalance = uiState.accounts.firstOrNull()?.totalBalance ?: 0.0,
                idCurrency = uiState.accounts.firstOrNull()?.idCurrencyAccount
                    ?: CurrencyType.Dollar.id,
                accountNumber = uiState.accounts.firstOrNull()?.accountNumber ?: "",
                ibanAccountNumber = uiState.accounts.firstOrNull()?.ibanAccountNumber ?: "",
                accountToken = uiState.accounts.firstOrNull()?.accountToken ?: ""
            )
        }
        if (comingFromDetails) {
            val previouslySelectedCrypto =
                uiState.userCryptoBalances.find { it.asset == marketCryptoCoin?.baseAsset }
            uiState = uiState.copy(
                assetBalanceDollars = previouslySelectedCrypto?.balanceDollars,
                assetAvailable = previouslySelectedCrypto?.available
            )
        }
    }

    private fun previousStep() {
        if (currentFlowStep == PurchaseCryptoSteps.One.pageNumber) {
            navigateBackToHome()
        } else {
            currentFlowStep--
            uiState = uiState.copy(
                currentStep = currentFlowStep
            )
            uiState.previousAction()
        }
    }

    private fun nextStep() {
        currentFlowStep++
        uiState = uiState.copy(
            currentStep = currentFlowStep
        )
        uiState.nextAction()
    }

    private fun navigateBackToHome() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.COLLAPSED
        )

    private fun onShowBottomSheet() {
        uiState = if (uiState.bottomSheetState.isVisible) {
            uiState.copy(
                bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
            )
        } else {
            uiState.copy(
                bottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)
            )
        }
    }

    private fun onCloseClick() {
        uiState = uiState.copy(
            openDialog = DialogParameters(
                titleResource = R.string.crypto_purchase_flow_exit_dialog_title,
                descriptionResource = R.string.crypto_sell_flow_dialog_cancel_message,
                positiveResource = R.string.crypto_purchase_flow_exit_dialog_cancel_button,
                negativeResource = R.string.button_continue,
                positiveAction = { navigateBackToHome() },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onSetupAccountDetails(
        smartAccountAvailableBalance: Double,
        idCurrency: Int,
        accountNumber: String,
        ibanAccountNumber: String,
        accountToken: String
    ) {
        uiState = uiState.copy(
            smartAccountAvailableBalance = smartAccountAvailableBalance,
            idCurrency = idCurrency,
            accountNumber = accountNumber,
            ibanAccountNumber = ibanAccountNumber,
            accountToken = accountToken
        )
    }

    data class UIState(
        val currentStep: Int = PurchaseCryptoSteps.One.pageNumber,
        val isLoading: Boolean = false,
        val accounts: List<SmartAccountSmall> = listOf(),
        val userCryptoBalances: List<BalanceCryptoAccountItems> = listOf(),
        val openDialog: DialogParameters = DialogParameters(),
        var bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
        var bottomSheet: (@Composable () -> Unit) = {},
        val smartAccountAvailableBalance: Double = 0.0,
        val accountNumber: String = "",
        val ibanAccountNumber: String = "",
        var shouldDisplayDisclaimer: Boolean = true,
        val dontShowAgainChecked: Boolean = false,
        val idCurrency: Int = CurrencyType.Dollar.id,
        val asset: String? = null,
        val assetDescription: String? = null,
        val market: String? = "",
        val cryptoNetWork: String? = "",
        val assetImageBaseUrl: String? = "",
        val assetAvailable: Double? = 0.0,
        val assetBalanceDollars: Double? = 0.0,
        val accountToken: String = "",
        val comingFromDetails: Boolean = false,
        var previousAction: () -> Unit = {},
        val nextAction: () -> Unit = {},
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnNextStep -> nextStep()
            is UIEvent.OnPreviousStep -> previousStep()
            is UIEvent.OnClickBottomSheet -> onShowBottomSheet()
            is UIEvent.OnCryptoSelected -> {
                uiState = uiState.copy(
                    asset = event.selectedCrypto.asset,
                    assetDescription = event.selectedCrypto.descriptionCurrency,
                    cryptoNetWork = event.selectedCrypto.cryptoNetwork,
                    assetImageBaseUrl = event.selectedCrypto.url_image,
                    market = event.selectedCrypto.asset.plus(CurrencyType.Dollar.disbursementValue),
                    assetAvailable = event.selectedCrypto.available,
                    assetBalanceDollars = event.selectedCrypto.balanceDollars
                )
            }
            is UIEvent.OnGetUserInfo -> setUserData()
            is UIEvent.OnSetSelectedAccount -> onSetupAccountDetails(
                event.smartAccountAvailableBalance,
                event.idCurrency,
                event.accountNumber,
                event.ibanAccountNumber,
                event.accountToken
            )
        }
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        object OnNextStep : UIEvent()
        object OnPreviousStep : UIEvent()
        object OnClickBottomSheet : UIEvent()
        data class OnCryptoSelected(
            val selectedCrypto: BalanceCryptoAccountItems
        ) : UIEvent()

        data class OnSetSelectedAccount(
            val smartAccountAvailableBalance: Double,
            val idCurrency: Int,
            val accountToken: String,
            val accountNumber: String,
            val ibanAccountNumber: String
        ) : UIEvent()

        object OnGetUserInfo : UIEvent()

    }

    companion object {
        const val DEFAULT_ID_BRAND_ERROR = -1
    }
}