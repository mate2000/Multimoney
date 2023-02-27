@file:OptIn(ExperimentalMaterialApi::class)

package com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails

import android.content.Intent
import android.net.Uri
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.domain.interaction.crypto.GetCurrencyHistoricalPricesUseCase
import com.multimoney.domain.interaction.crypto.GetCurrencyNewsUseCase
import com.multimoney.domain.model.crypto.CryptoNewsFeed
import com.multimoney.domain.model.crypto.CurrencyHistoricPrice
import com.multimoney.domain.model.crypto.MarketCryptoCoin
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.DESCRIPTION_CURRENCY
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.navgraph.ITEM_CRYPTO_MARKET
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.navigation.util.encodeData
import com.multimoney.multimoney.presentation.ui.crypto.CryptoProcessErrorCodes
import com.multimoney.multimoney.presentation.util.CryptoHelper
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDateYMDPattern
import com.multimoney.multimoney.presentation.util.getPreviousDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketCurrencyDetailsViewModel @Inject constructor(
    private val queryGetCurrencyHistoricalPricesUseCase: GetCurrencyHistoricalPricesUseCase,
    private val queryGetCurrencyNewsUseCase: GetCurrencyNewsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
    private val cryptoHelper: CryptoHelper
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UiState())
        private set

    //stateless
    val defaultDialogParameters = DialogParameters(descriptionResource = R.string.something_went_wrong)

    private fun setPreviousInfo() {
        uiState = uiState.copy(
            user = savedStateHandle[USER] ?: "",
            selectedCryptoCoin = savedStateHandle[ITEM_CRYPTO_MARKET],
            idBrand = savedStateHandle[ID_BRAND]
        )
        viewModelScope.launch {
            uiState = uiState.copy(
                shouldDisplayDisclaimer = dataStorePreferences.isVolatileDialogVisible().first(),
                isCryptoTransferEnabled = cryptoHelper.isCryptoTransferEnabled()
            )
        }
    }

    private fun getCurrencyHistoricalPrices(
        dataPoints: Long,
        daysToSubtract: Long
    ) = executeUseCase {
        queryGetCurrencyHistoricalPricesUseCase.invoke(
            market = uiState.selectedCryptoCoin?.baseAsset.plus(CurrencyType.Dollar.disbursementValue),
            max_data_points = dataPoints,
            range_begin = getPreviousDate(daysToSubtract),
            range_end = getCurrentDateYMDPattern(),
            pagination_limit = HISTORICAL_CURRENCY_PRICES_LIMIT_MAX,
            pagination_offset = HISTORICAL_CURRENCY_PRICES_OFFSET,
            user = uiState.user ?: "",
            idBrand = uiState.idBrand ?: 0
        ).collectLatest { result ->
            result.onSuccess { getHistoricalCurrencyPrices ->
                uiState = uiState.copy(
                    isLoading = false,
                    getHistoricalCurrencyPrices = getHistoricalCurrencyPrices.cryptoHistoricalPrice.items
                )
            }
            result.onFailure {
                if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                    navigateToMaintenance()
                    return@onFailure
                }
                onFailure(it)
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun getCurrencyNews() = executeUseCase {
        queryGetCurrencyNewsUseCase.invoke(
            user = uiState.user ?: "",
            idBrand = uiState.idBrand ?: 0,
            baseAsset = uiState.selectedCryptoCoin?.baseAsset ?: ""
        ).collectLatest { result ->
            result.onSuccess { currencyNews ->
                currencyNews.let {
                    uiState = uiState.copy(
                        isLoading = false,
                        currencyNews = it.cryptoNewsFeed
                    )
                }
            }
            result.onFailure {
                if (it.errorCode == CryptoProcessErrorCodes.Maintenance.status) {
                    navigateToMaintenance()
                    return@onFailure
                }
                onFailure(it)
            }
            result.onLoading {
                uiState = uiState.copy(isLoading = true)
            }
        }
    }

    private fun openCryptoNew(
        link: String,
        openCryptoNew: (Intent) -> Unit,
        onFailureWithDialog: (isActive: Boolean, dialogParameters: DialogParameters) -> Unit
    ) {
        try {
            val uri = Uri.parse(link)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            openCryptoNew(intent)

        } catch (e: NullPointerException) {
            onFailureWithDialog(
                false,
                defaultDialogParameters.copy(isActive = mutableStateOf(true))
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

    private fun onDisclaimerChecked(checked: Boolean) {
        uiState = uiState.copy(dontShowAgainChecked = checked)
    }

    private fun updateShouldShowDisclaimer(value: Boolean) {
        viewModelScope.launch {
            dataStorePreferences.setVolatileDialogVisible(!value)
            uiState = uiState.copy(
                shouldDisplayDisclaimer = preferences.isVolatileDialogVisible().first()
            )
        }
    }

    private fun navigateToMaintenance() {
        navigateTo(Screen.MaintenanceAlertScreen.route)
    }

    private fun onNavigateToSelectAccount(){
        navigateTo("${Screen.PurchaseCryptoFlow.baseRoute}/${Screen.CryptoCurrencyDetailsScreen.baseRoute}?$ITEM_CRYPTO_MARKET=${encodeData(uiState.selectedCryptoCoin)}")
    }

    private fun onNavigateToSellCrypto(){
        navigateTo("${Screen.CryptoSellFlow.baseRoute}/${Screen.CryptoCurrencyDetailsScreen.baseRoute}?$ITEM_CRYPTO_MARKET=${encodeData(uiState.selectedCryptoCoin)}")
    }

    private fun onNavigateToCryptoSendFlow() {
        navigateTo("${Screen.CryptoSendFlow.baseRoute}?$CRYPTO_ASSET=${uiState.selectedCryptoCoin?.baseAsset}&$DESCRIPTION_CURRENCY=${uiState.selectedCryptoCoin?.description}")
    }

    private fun onNavigateToCryptoReceiveFlow() {
        navigateTo("${Screen.CryptoReceiveFlowScreen.baseRoute}/${uiState.user}/${uiState.idBrand}?$ITEM_CRYPTO_MARKET=${encodeData(uiState.selectedCryptoCoin)}")
    }

    data class UiState(
        val user: String? = null,
        val idBrand: Int? = null,
        val selectedCryptoCoin: MarketCryptoCoin? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val currencyNews: CryptoNewsFeed? = null,
        val getHistoricalCurrencyPrices: List<CurrencyHistoricPrice> = emptyList(),
        val shouldDisplayDisclaimer: Boolean = true,
        val dontShowAgainChecked: Boolean = false,
        val bottomSheetVisibleState: ModalBottomSheetState = ModalBottomSheetState(
            ModalBottomSheetValue.Hidden),
        val isCryptoTransferEnabled: Boolean = false
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.CryptoMarketScreen.route, false)
            is UIEvent.OnSetPreviousInfo -> setPreviousInfo()
            is UIEvent.OnGetCurrencyNews -> getCurrencyNews()
            is UIEvent.OnGetCurrencyHistoricalPrices -> getCurrencyHistoricalPrices(
                dataPoints = event.dataPoints,
                daysToSubtract = event.daysToSubtract
            )
            is UIEvent.OnOpenCryptoNew -> openCryptoNew(
                event.link,
                event.openCryptoNew,
                event.onFailureWithDialog
            )
            is UIEvent.OnFailureWithDialog -> uiState = uiState.copy(
                isLoading = event.isLoading,
                openDialog = event.dialogParameters
            )
            is UIEvent.OnNavigateToSelectAccount -> onNavigateToSelectAccount()
            is UIEvent.OnNavigateToSellCrypto -> onNavigateToSellCrypto()
            is UIEvent.OnNavigateToReceiveCrypto -> onNavigateToCryptoReceiveFlow()
            is UIEvent.OnDisclaimerChecked -> onDisclaimerChecked(event.checked)
            is UIEvent.OnUpdateShouldShowDisclaimer -> updateShouldShowDisclaimer(event.checked)
            is UIEvent.OnShowDisclaimer -> uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Expanded))
            is UIEvent.OnHideDisclaimer -> uiState = uiState.copy(bottomSheetVisibleState = ModalBottomSheetState(ModalBottomSheetValue.Hidden))
            is UIEvent.OnNavigateToCryptoSendFlow -> onNavigateToCryptoSendFlow()
        }
    }

    sealed class UIEvent {
        object OnNavigateBack : UIEvent()
        object OnSetPreviousInfo : UIEvent()
        object OnGetCurrencyNews : UIEvent()
        data class OnGetCurrencyHistoricalPrices(
            val dataPoints: Long = MAXIMUM_DATA_POINTS,
            val daysToSubtract: Long = FilterDateByDays.YESTERDAY.time
        ) : UIEvent()
        data class OnOpenCryptoNew(
            val link: String,
            val openCryptoNew: (Intent) -> Unit,
            val onFailureWithDialog: (isLoading: Boolean, dialogParameters: DialogParameters) -> Unit
        ) : UIEvent()
        data class OnFailureWithDialog(
            val isLoading: Boolean,
            val dialogParameters: DialogParameters
        ) : UIEvent()
        object OnNavigateToSelectAccount : UIEvent()

        data class OnDisclaimerChecked(val checked: Boolean) : UIEvent()
        data class OnUpdateShouldShowDisclaimer(val checked: Boolean) : UIEvent()
        object OnShowDisclaimer : UIEvent()
        object OnHideDisclaimer : UIEvent()
        object OnNavigateToCryptoSendFlow : UIEvent()
        object OnNavigateToSellCrypto : UIEvent()
        object OnNavigateToReceiveCrypto : UIEvent()
    }
}

const val MAXIMUM_DATA_POINTS = 125L
const val HISTORICAL_CURRENCY_PRICES_OFFSET = 0
const val HISTORICAL_CURRENCY_PRICES_LIMIT_MAX = 1000
