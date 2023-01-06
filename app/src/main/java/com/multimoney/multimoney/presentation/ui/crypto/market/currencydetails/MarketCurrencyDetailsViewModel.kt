package com.multimoney.multimoney.presentation.ui.crypto.market.currencydetails

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import com.multimoney.domain.interaction.crypto.GetCurrencyHistoricalPricesUseCase
import com.multimoney.domain.interaction.crypto.GetCurrencyNewsUseCase
import com.multimoney.domain.model.crypto.CryptoNewsFeed
import com.multimoney.domain.model.crypto.CurrencyHistoricPrice
import com.multimoney.domain.model.util.error.HttpError
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import com.multimoney.multimoney.presentation.navigation.CRYPTO_ASSET
import com.multimoney.multimoney.presentation.navigation.CURRENT_CRYPTO_PRICE
import com.multimoney.multimoney.presentation.navigation.DESCRIPTION_CURRENCY
import com.multimoney.multimoney.presentation.navigation.ID_BRAND
import com.multimoney.multimoney.presentation.navigation.Screen
import com.multimoney.multimoney.presentation.navigation.URL_IMAGE
import com.multimoney.multimoney.presentation.navigation.navgraph.USER
import com.multimoney.multimoney.presentation.util.FilterDateByDays
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getCurrentDateYMDPattern
import com.multimoney.multimoney.presentation.util.getPreviousDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MarketCurrencyDetailsViewModel @Inject constructor(
    private val queryGetCurrencyHistoricalPricesUseCase: GetCurrencyHistoricalPricesUseCase,
    private val queryGetCurrencyNewsUseCase: GetCurrencyNewsUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(true) {

    var uiState by mutableStateOf(UiState())
        private set

    //stateless
    val defaultDialogParameters = DialogParameters(descriptionResource = R.string.something_went_wrong)

    private fun setPreviousInfo() {
        uiState = uiState.copy(
            user = savedStateHandle[USER] ?: "",
            description = savedStateHandle[DESCRIPTION_CURRENCY] ?: "",
            idBrand = savedStateHandle[ID_BRAND] ?: 0,
            asset = savedStateHandle[CRYPTO_ASSET] ?: "",
            currentPrice = savedStateHandle[CURRENT_CRYPTO_PRICE] ?: 0.0f,
            urlImage = savedStateHandle[URL_IMAGE] ?: ""
        )
    }

    private fun getCurrencyHistoricalPrices(
        dataPoints: Long,
        daysToSubtract: Long
    ) = executeUseCase {
        queryGetCurrencyHistoricalPricesUseCase.invoke(
            market = uiState.asset.plus(CurrencyType.Dollar.disbursementValue),
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
            baseAsset = uiState.asset ?: ""
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

    data class UiState(
        val user: String? = null,
        val idBrand: Int? = null,
        val asset: String? = null,
        val description: String? = null,
        val currentPrice: Float? = null,
        val urlImage: String? = null,
        val isLoading: Boolean = false,
        val openDialog: DialogParameters = DialogParameters(),
        val currencyNews: CryptoNewsFeed? = null,
        val getHistoricalCurrencyPrices: List<CurrencyHistoricPrice> = emptyList()
    )

    fun onUIEvent(event: UIEvent) {
        when (event) {
            is UIEvent.OnNavigateBack -> navigateBack(Screen.HomeScreen.route, false)
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
    }
}

const val MAXIMUM_DATA_POINTS = 125L
const val HISTORICAL_CURRENCY_PRICES_OFFSET = 0
const val HISTORICAL_CURRENCY_PRICES_LIMIT_MAX = 1000