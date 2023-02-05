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
import com.multimoney.data.util.catalog.PurchaseCryptoSteps
import com.multimoney.domain.interaction.accountsmart.QuerySmartAccountsUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartForBuyCrypto
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_MARKET
import com.multimoney.multimoney.presentation.ui.crypto.CryptoOperationSide
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class PurchaseCryptoSharedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
    private val getSmartAccountsUseCase: QuerySmartAccountsUseCase
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
    var abvCurrency: String = CurrencyType.Dollar.disbursementValue
    val side = CryptoOperationSide.BUY.value
    val comingFromDetails: Boolean = marketCryptoCoin != null

    private fun setUserData() {
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
    }

    private fun querySmartAccounts() {
        executeUseCase {
            getSmartAccountsUseCase(
                user = email,
                idBrand = idBrand,
                identification = identification,
                accountStatus = ACTIVE_ACCOUNT
            ).collectLatest { result ->
                result.onSuccess {
                    it?.let {
                        uiState = uiState.copy(accounts = it)
                    }
                }
                result.onFailure {
                    onFailure(it)
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

    private fun previousStep() {
        if (currentFlowStep == PurchaseCryptoSteps.One.pageNumber) {
            navigateBackToHome()
        } else {
            currentFlowStep--
            uiState = uiState.copy(
                currentStep = currentFlowStep
            )
        }
    }

    private fun nextStep() {
        currentFlowStep++
        uiState = uiState.copy(
            currentStep = currentFlowStep
        )
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

    private fun OnShowDisclaimer() {
        emitBaseEvent(BaseEvent.OnShowDisclaimer)
    }

    private fun updateShouldShowDisclaimer(value: Boolean) {
        viewModelScope.launch {
            dataStorePreferences.setVolatileDialogVisible(!value)
            uiState = uiState.copy(shouldDisplayDisclaimer = preferences.isVolatileDialogVisible().first())
        }
    }

    private fun onDisclaimerChecked(checked: Boolean) {
        uiState = uiState.copy(dontShowAgainChecked = checked)
    }

    data class UIState(
        val currentStep: Int = PurchaseCryptoSteps.One.pageNumber,
        val isLoading: Boolean = false,
        val accounts: List<AccountSmartForBuyCrypto> = listOf(),
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
        val accountToken: String = "",
        val comingFromDetails: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNextStep -> nextStep()
            is UIEvent.OnPreviousStep -> previousStep()
            is UIEvent.OnClickBottomSheet -> onShowBottomSheet()
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnGetUserInfo -> setUserData()
            is UIEvent.OnQueryAccounts -> querySmartAccounts()
            is UIEvent.OnCryptoSelected -> {
                uiState = uiState.copy(
                    asset = event.selectedCrypto.baseAsset,
                    assetDescription = event.selectedCrypto.description,
                    cryptoNetWork = event.selectedCrypto.cryptoNetwork,
                    assetImageBaseUrl = event.selectedCrypto.url_image,
                    market = event.selectedCrypto.baseAsset.plus(CurrencyType.Dollar.disbursementValue)
                )
            }
            is UIEvent.OnSetSelectedAccount -> {
                uiState = uiState.copy(
                    smartAccountAvailableBalance = event.totalBalance,
                    idCurrency = event.idCurrency,
                    accountNumber = event.accountNumber,
                    ibanAccountNumber = event.ibanAccountNumber,
                )
            }
            is BaseEvent.OnShowDisclaimer -> OnShowDisclaimer()
            is UIEvent.OnDisclaimerChecked -> onDisclaimerChecked(event.checked)
            is UIEvent.OnUpdateShouldShowDisclaimer -> updateShouldShowDisclaimer(event.checked)
        }
    }

    sealed class BaseEvent {
        object OnShowDisclaimer : UIEvent()
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        object OnNextStep : UIEvent()
        object OnQueryAccounts : UIEvent()
        object OnPreviousStep : UIEvent()
        object OnClickBottomSheet : UIEvent()
        data class OnCryptoSelected(
            val selectedCrypto: MarketCryptoCoin
        ) : UIEvent()

        data class OnSetSelectedAccount(
            val totalBalance: Double,
            val idCurrency: Int,
            val accountToken: String,
            val accountNumber: String,
            val ibanAccountNumber: String
        ) : UIEvent()

        object OnGetUserInfo : UIEvent()
        data class OnDisclaimerChecked(val checked: Boolean) : UIEvent()
        data class OnUpdateShouldShowDisclaimer(val checked: Boolean) : UIEvent()

    }

    companion object {
        const val ACTIVE_ACCOUNT = 1
        const val DEFAULT_ID_BRAND_ERROR = -1
    }
}