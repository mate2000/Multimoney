package com.multimoney.multimoney.presentation.ui.crypto.purchase

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.data.util.catalog.PurchaseCryptoSteps
import com.multimoney.domain.interaction.accountsmart.QuerySmartAccountsUseCase
import com.multimoney.domain.model.accountsmart.AccountSmartForBuyCrypto
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.DESCRIPTION_CURRENCY
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.IDENTIFICATION
import com.multimoney.multimoney.presentation.navigation.navgraph.PK_USER
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.ui.crypto.CryptoOperationSide
import com.multimoney.multimoney.presentation.ui.home.HomeState
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@HiltViewModel
class PurchaseCryptoSharedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
    private val getSmartAccountsUseCase: QuerySmartAccountsUseCase
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UIState())
        private set

    //stateless
    private var overridePreviousAction: (() -> Unit)? = null
    private var nextStep: Int = PurchaseCryptoSteps.One.id
    private var previousStep: Int = PurchaseCryptoSteps.One.id
    var nextAction: () -> Unit = {}

    //bundle parameters
    // la necesaria
    val status = 1
    var idBrand = 0
    var identification = ""
    var email = ""
    var pkUser = ""
    var asset: String? = savedStateHandle[CRYPTO_ASSET]
    var assetDescription: String? = savedStateHandle[DESCRIPTION_CURRENCY] ?: ""
    val comingFromDetails: Boolean = asset != null
    val market = asset.plus(CurrencyType.Dollar.disbursementValue)
    val side = CryptoOperationSide.BUY.value

    // la de smart
    //val cryptoNetwork: String = ""
    val accountToken: String = ""
    var abvCurrency: String = ""

    private fun setUserData() {
        viewModelScope.launch {
            idBrand = dataStorePreferences.getIdBrand().first().toInt()
            pkUser = dataStorePreferences.getPkUser().first()
            identification = dataStorePreferences.getIdentification().first()
            email = dataStorePreferences.getUserEmail().first()
            abvCurrency = if (idBrand == Brand.CostaRica.id) {
                CurrencyType.Colon.disbursementValue
            } else {
                CurrencyType.Dollar.disbursementValue
            }
            if (comingFromDetails){
                uiState = uiState.copy(asset = asset ?: "", assetDescription = assetDescription ?: "")
            }
        }
    }

    private fun querySmartAccounts() {
        executeUseCase {
            getSmartAccountsUseCase(
                user = email,
                idBrand = idBrand,
                identification = identification,
                accountStatus = ACTIVE_ACCOUNT
            ).collectLatest {result ->
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
        if (overridePreviousAction != null) {
            overridePreviousAction?.invoke()
        } else {
            if (previousStep > PurchaseCryptoSteps.One.id || uiState.currentStep == PurchaseCryptoSteps.Two.id) {
                uiState = uiState.copy(
                    currentStep = previousStep
                )
            } else {
                navigateBackToHome()
            }
        }
    }

    private fun getTotalSteps(): Int {
        val counter = if (idBrand == Brand.CostaRica.id) {
            PURCHASE_CRYPTO_TOTAL_STEPS_CR
        } else if (idBrand == Brand.ElSalvador.id) {
            PURCHASE_CRYPTO_TOTAL_STEPS_SV
        } else if (idBrand == Brand.CostaRica.id && comingFromDetails) {
            PURCHASE_CRYPTO_TOTAL_STEPS_CR_DETAILS
        } else {
            PURCHASE_CRYPTO_TOTAL_STEPS_SV_DETAILS
        }
        return counter
    }

    private fun overridePreviousAction(overridePreviousAction: (() -> Unit)?) {
        this.overridePreviousAction = overridePreviousAction
    }

    private fun nextStep() {
        if (nextStep <= getTotalSteps()) {
            uiState = uiState.copy(
                currentStep = nextStep
            )
        }
    }

    private fun navigateBackToHome() =
        navigateBack(
            popTo = Screen.HomeScreen.route,
            isRestart = true,
            homeState = HomeState.UNEXPANDED
        )

    private fun onSetNavigation(
        nextAction: () -> Unit,
        overridePreviousAction: (() -> Unit)?,
        nextStep: Int,
        previousStep: Int
    ) {
        this.nextAction = nextAction
        this.overridePreviousAction = overridePreviousAction
        this.nextStep = nextStep
        this.previousStep = previousStep
    }

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
                titleResource = R.string.smart_close_origination_dialog_title,
                positiveResource = R.string.common_leave,
                negativeResource = R.string.button_continue,
                positiveAction = { navigateBackToHome() },
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        val currentStep: Int = 1,
        val isLoading: Boolean = false,
        val accounts: List<AccountSmartForBuyCrypto> = listOf(),
        val openDialog: DialogParameters = DialogParameters(),
        var bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
        var bottomSheet: (@Composable () -> Unit) = {},
        var asset: String = "",
        var assetDescription: String = ""
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnSetNavigation -> onSetNavigation(
                nextAction = event.nextAction,
                overridePreviousAction = event.overridePreviousAction,
                nextStep = event.nextStep,
                previousStep = event.previousStep
            )
            is UIEvent.OnNextStep -> nextStep()
            is UIEvent.OnPreviousStep -> previousStep()
            is UIEvent.OnClickBottomSheet -> onShowBottomSheet()
            is UIEvent.OverridePreviousAction -> overridePreviousAction(event.action)
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnGetUserInfo -> setUserData()
            is UIEvent.OnQueryAccounts -> querySmartAccounts()
            is UIEvent.OnCryptoSelected -> onCryptoSelected(event.selectedCrypto)
        }
    }

    private fun onCryptoSelected(selectedCrypto: MarketCryptoCoin) {
        uiState = uiState.copy(asset = selectedCrypto.baseAsset, assetDescription = selectedCrypto.description)
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        data class OnSetNavigation(
            val nextAction: () -> Unit = {},
            val overridePreviousAction: (() -> Unit)? = null,
            val nextStep: Int,
            val previousStep: Int
        ) : UIEvent()

        object OnNextStep : UIEvent()
        object OnQueryAccounts : UIEvent()
        object OnPreviousStep : UIEvent()
        object OnClickBottomSheet : UIEvent()
        data class OverridePreviousAction(val action: (() -> Unit)?) : UIEvent()
        data class OnCryptoSelected(
            val selectedCrypto: MarketCryptoCoin
        ) : UIEvent()
        object OnGetUserInfo : UIEvent()
    }

    companion object {
        const val ACTIVE_ACCOUNT = 1
        const val DEFAULT_ID_BRAND_ERROR = -1
        const val PURCHASE_CRYPTO_TOTAL_STEPS_CR = 4
        const val PURCHASE_CRYPTO_TOTAL_STEPS_SV = 3
        const val PURCHASE_CRYPTO_TOTAL_STEPS_CR_DETAILS = 3
        const val PURCHASE_CRYPTO_TOTAL_STEPS_SV_DETAILS = 2
    }
}