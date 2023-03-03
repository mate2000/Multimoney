package com.multimoney.multimoney.presentation.ui.crypto.purchase

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
import com.multimoney.data.util.catalog.BuyCryptoStep
import com.multimoney.data.util.catalog.PurchaseCryptoSteps
import com.multimoney.domain.model.accountsmart.SmartAccountSmall
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_MARKET
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.ui.crypto.CryptoOperationSide
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDate
import com.multimoney.multimoney.presentation.util.getCurrentTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class PurchaseCryptoSharedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    // stateless
    private var currentFlowStep: Int = PurchaseCryptoSteps.One.pageNumber

    // bundle parameters
    var idBrand = DEFAULT_ID_BRAND_ERROR
    var pkUser = ""
    var user = ""
    var identification = ""
    var email = ""
    private var marketCryptoCoin: MarketCryptoCoin? = savedStateHandle[ITEM_CRYPTO_MARKET]
    var abvCurrency: String = CurrencyType.Dollar.disbursementValue
    val side = CryptoOperationSide.BUY.value
    val comingFromDetails: Boolean = marketCryptoCoin != null
    var previousScreen: String = ""

    private fun setUserData() {
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""
        viewModelScope.launch {
            idBrand = dataStorePreferences.getIdBrand().first().toInt()
            pkUser = dataStorePreferences.getPkUser().first()
            user = dataStorePreferences.getUserName().first()
            identification = dataStorePreferences.getIdentification().first()
            email = dataStorePreferences.getUserEmail().first()
            uiState = uiState.copy(
                asset = marketCryptoCoin?.baseAsset,
                assetDescription = marketCryptoCoin?.description,
                market = marketCryptoCoin?.baseAsset?.plus(abvCurrency),
                cryptoNetWork = marketCryptoCoin?.cryptoNetwork,
                assetImageBaseUrl = marketCryptoCoin?.url_image,
                shouldDisplayDisclaimer = preferences.isVolatileDialogVisible().first()
            )
        }
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

    private fun previousStep() {
        if (currentFlowStep == PurchaseCryptoSteps.One.pageNumber) {
            navigateToPreviousScreen()
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

    private fun navigateToPreviousScreen() {
        val screen = when (previousScreen) {
            Screen.CryptoWalletScreen.baseRoute -> Screen.CryptoWalletScreen.route
            Screen.CryptoCurrencyDetailsScreen.baseRoute -> Screen.CryptoCurrencyDetailsScreen.route
            Screen.CryptoWalletDetailsScreen.baseRoute -> Screen.CryptoWalletDetailsScreen.route
            else -> Screen.HomeScreen.route
        }
        navigateBack(popTo = screen, isRestart = false)
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
                descriptionResource = R.string.crypto_purchase_flow_exit_dialog_body_message,
                positiveResource = R.string.crypto_purchase_flow_exit_dialog_cancel_button,
                negativeResource = R.string.button_continue,
                positiveAction = { navigateBackToHome() },
                isActive = mutableStateOf(true)
            )
        )
    }

    private fun onShowDisclaimer() {
        emitBaseEvent(BaseEvent.OnShowDisclaimer)
    }

    private fun onShowMaintenanceAlert() {
        emitBaseEvent(BaseEvent.OnShowMaintenance)
    }

    private fun updateShouldShowDisclaimer(value: Boolean) {
        viewModelScope.launch {
            dataStorePreferences.setVolatileDialogVisible(!value)
            uiState = uiState.copy(
                shouldDisplayDisclaimer = preferences.isVolatileDialogVisible().first()
            )
        }
    }

    private fun onDisclaimerChecked(checked: Boolean) {
        uiState = uiState.copy(dontShowAgainChecked = checked)
    }

    private fun onSetupVoucherDetails(
        quoteAmount: String,
        baseAmount: String,
        totalDebitedAmount: String,
        exchangeRate: String,
        totalDebitedExchange: String,
        referenceNumber: String
    ) {
        uiState = uiState.copy(
            voucherQuoteAmount = quoteAmount,
            voucherBaseAmount = baseAmount,
            voucherReferenceNumber = referenceNumber,
            voucherTotalDebitedAmount = totalDebitedAmount,
            voucherExchangeRate = exchangeRate,
            voucherTotalDebitedExchange = totalDebitedExchange,
            purchaseCurrentDate = getCurrentDate(Calendar.getInstance().time),
            purchaseCurrentTime = getCurrentTime(Calendar.getInstance().time)
        )
    }

    data class UIState(
        // interaction
        val currentStep: Int = PurchaseCryptoSteps.One.pageNumber,
        val currentStepType: BuyCryptoStep = BuyCryptoStep.LIST_CRYPTO_CURRENCIES,
        val isLoading: Boolean = false,
        val isPaxosInMaintenance: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        var bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
        var bottomSheet: (@Composable () -> Unit) = {},
        var shouldDisplayDisclaimer: Boolean = true,
        val dontShowAgainChecked: Boolean = false,
        val comingFromDetails: Boolean = false,
        var previousAction: () -> Unit = {},
        val nextAction: () -> Unit = {},
        // mutable data
        val accounts: List<SmartAccountSmall> = listOf(),
        val smartAccountAvailableBalance: Double = 0.0,
        val accountNumber: String = "",
        val ibanAccountNumber: String = "",
        val idCurrency: Int = CurrencyType.Dollar.id,
        val asset: String? = null,
        val assetDescription: String? = null,
        val market: String? = "",
        val cryptoNetWork: String? = "",
        val assetImageBaseUrl: String? = "",
        val accountToken: String = "",
        // voucher information
        val voucherQuoteAmount: String? = null,
        val voucherBaseAmount: String? = null,
        val voucherReferenceNumber: String? = null,
        val voucherTotalDebitedAmount: String? = null,
        val voucherExchangeRate: String? = null,
        val voucherTotalDebitedExchange: String? = null,
        val purchaseCurrentDate: String? = null,
        val purchaseCurrentTime: String? = null,
        val isRightButtonVisible: Boolean = true,
        val isLeftButtonVisible: Boolean = true

    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNextStep -> nextStep()
            is UIEvent.OnPreviousStep -> previousStep()
            is UIEvent.OnClickBottomSheet -> onShowBottomSheet()
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnGetUserInfo -> setUserData()
            is UIEvent.OnCryptoSelected -> {
                uiState = uiState.copy(
                    asset = event.selectedCrypto.baseAsset,
                    assetDescription = event.selectedCrypto.description,
                    cryptoNetWork = event.selectedCrypto.cryptoNetwork,
                    assetImageBaseUrl = event.selectedCrypto.url_image,
                    market = event.selectedCrypto.baseAsset.plus(CurrencyType.Dollar.disbursementValue)
                )
            }
            is UIEvent.OnSetSelectedAccount -> onSetupAccountDetails(
                event.smartAccountAvailableBalance,
                event.idCurrency,
                event.accountNumber,
                event.ibanAccountNumber,
                event.accountToken
            )

            is BaseEvent.OnShowDisclaimer -> onShowDisclaimer()
            is BaseEvent.OnShowMaintenance -> onShowMaintenanceAlert()
            is UIEvent.OnDisclaimerChecked -> onDisclaimerChecked(event.checked)
            is UIEvent.OnUpdateShouldShowDisclaimer -> updateShouldShowDisclaimer(event.checked)

            is UIEvent.OnSetupVoucherDetails -> onSetupVoucherDetails(
                event.quoteAmount,
                event.baseAmount,
                event.totalDebitedAmount,
                event.exchangeRate,
                event.totalDebitedExchange,
                event.referenceNumber
            )
            is UIEvent.OnSetFlowStep -> uiState = uiState.copy(currentStepType = event.step)
            is UIEvent.OnNavigateHome -> navigateBack(
                popTo = Screen.HomeScreen.route,
                isRestart = true,
                homeState = HomeState.COLLAPSED
            )
            is UIEvent.OnSetSmartAccounts -> uiState = uiState.copy(accounts = event.accounts)
            is UIEvent.SetPaxosMaintenanceState -> uiState = uiState.copy(isPaxosInMaintenance = event.isPaxosInMaintenance)
        }
    }

    sealed class BaseEvent {
        object OnShowDisclaimer : UIEvent()
        object OnShowMaintenance : UIEvent()
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        object OnNextStep : UIEvent()
        data class OnSetupVoucherDetails(
            val quoteAmount: String,
            val baseAmount: String,
            val totalDebitedAmount: String,
            val exchangeRate: String,
            val totalDebitedExchange: String,
            val referenceNumber: String
        ) : UIEvent()

        object OnPreviousStep : UIEvent()
        object OnClickBottomSheet : UIEvent()
        data class OnCryptoSelected(
            val selectedCrypto: MarketCryptoCoin
        ) : UIEvent()

        data class OnSetSelectedAccount(
            val smartAccountAvailableBalance: Double,
            val idCurrency: Int,
            val accountToken: String,
            val accountNumber: String,
            val ibanAccountNumber: String
        ) : UIEvent()

        object OnGetUserInfo : UIEvent()
        data class OnDisclaimerChecked(val checked: Boolean) : UIEvent()
        data class OnUpdateShouldShowDisclaimer(val checked: Boolean) : UIEvent()
        data class OnSetFlowStep(val step: BuyCryptoStep) : UIEvent()
        object OnNavigateHome : UIEvent()
        data class OnSetSmartAccounts(val accounts: List<SmartAccountSmall>) : UIEvent()
        data class SetPaxosMaintenanceState(val isPaxosInMaintenance: Boolean) : UIEvent()
    }

    companion object {
        const val DEFAULT_ID_BRAND_ERROR = -1
    }
}
