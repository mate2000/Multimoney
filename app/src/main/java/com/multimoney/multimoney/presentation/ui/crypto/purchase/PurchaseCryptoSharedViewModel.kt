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
import com.multimoney.multimoney.presentation.navigation.Screen
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
    savedStateHandle: SavedStateHandle,
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
    var abvCurrency: String = ""
    var asset: String? = savedStateHandle[CRYPTO_ASSET]
    var assetDescription: String? = savedStateHandle[DESCRIPTION_CURRENCY] ?: ""
    var market = asset?.plus(CurrencyType.Dollar.disbursementValue)
    var cryptoNetWork = ""
    var assetImageBaseUrl = ""
    val side = CryptoOperationSide.BUY.value
    var accountToken: String = ""
    val comingFromDetails: Boolean = asset != null

    private fun setUserData() {
        viewModelScope.launch {
            idBrand = dataStorePreferences.getIdBrand().first().toInt()
            pkUser = dataStorePreferences.getPkUser().first()
            user = dataStorePreferences.getUserName().first()
            identification = dataStorePreferences.getIdentification().first()
            email = dataStorePreferences.getUserEmail().first()
            abvCurrency = if (idBrand == Brand.CostaRica.id) {
                CurrencyType.Colon.disbursementValue
            } else {
                CurrencyType.Dollar.disbursementValue
            }
            uiState = uiState.copy(
                isBottomSheetVisible = preferences.isVolatileDialogVisible().first()
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
            ).collectLatest {result ->
                result.onSuccess {
                    it?.let {
                        if (idBrand == Brand.ElSalvador.id) {
                            accountToken = it[0].accountToken
                        } else {
                            uiState = uiState.copy(accounts = it)
                        }
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
                titleResource = R.string.smart_close_origination_dialog_title,
                positiveResource = R.string.common_leave,
                negativeResource = R.string.button_continue,
                positiveAction = { navigateBackToHome() },
                isActive = mutableStateOf(true)
            )
        )
    }

    data class UIState(
        val currentStep: Int = PurchaseCryptoSteps.One.pageNumber,
        val isLoading: Boolean = false,
        val accounts: List<AccountSmartForBuyCrypto> = listOf(),
        val openDialog: DialogParameters = DialogParameters(),
        var bottomSheetState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
        var bottomSheet: (@Composable () -> Unit) = {},
        val smartAccountAvailableBalance: Double = 0.0,
        var isBottomSheetVisible: Boolean = true
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNextStep -> nextStep()
            is UIEvent.OnPreviousStep -> previousStep()
            is UIEvent.OnClickBottomSheet -> onShowBottomSheet()
            is UIEvent.OnCloseClick -> onCloseClick()
            is UIEvent.OnGetUserInfo -> setUserData()
            is UIEvent.OnQueryAccounts -> querySmartAccounts()
            is UIEvent.OnSetAccountToken -> accountToken = event.accountToken
            is UIEvent.OnCryptoSelected -> {
                asset = if (asset.isNullOrEmpty()) event.selectedCrypto.baseAsset else asset
                assetDescription = if (assetDescription.isNullOrEmpty()) event.selectedCrypto.description else assetDescription
                cryptoNetWork = event.selectedCrypto.cryptoNetwork
                assetImageBaseUrl = event.selectedCrypto.url_image
                market = asset.plus(CurrencyType.Dollar.disbursementValue)
            }
            is UIEvent.OnSetSelectedAccount -> {
                accountToken = event.accountToken
                uiState = uiState.copy(
                    smartAccountAvailableBalance = event.totalBalance
                )
            }
        }
    }

    sealed class UIEvent {
        object OnCloseClick : UIEvent()
        object OnNextStep : UIEvent()
        object OnQueryAccounts : UIEvent()
        data class OnSetAccountToken(val accountToken: String) : UIEvent()
        object OnPreviousStep : UIEvent()
        object OnClickBottomSheet : UIEvent()
        data class OnCryptoSelected(
            val selectedCrypto: MarketCryptoCoin
        ) : UIEvent()
        data class OnSetSelectedAccount(
            val accountToken: String,
            val totalBalance: Double
        ) : UIEvent()
        object OnGetUserInfo : UIEvent()
    }

    companion object {
        const val ACTIVE_ACCOUNT = 1
        const val DEFAULT_ID_BRAND_ERROR = -1
    }
}