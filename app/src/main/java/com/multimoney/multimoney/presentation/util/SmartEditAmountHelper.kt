package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.view.View
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.SavedStateHandle
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.data.util.catalog.Brand
import com.multimoney.domain.interaction.accountsmart.QuerySmartExchangeRateUseCase
import com.multimoney.domain.model.accountsmart.ExchangeRateResult
import com.multimoney.domain.model.accountsmart.IbanAccountID
import com.multimoney.domain.model.accountsmart.SmartAccountID
import com.multimoney.domain.model.util.onFailure
import com.multimoney.domain.model.util.onLoading
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.navigation.IBAN_ACCOUNT
import com.multimoney.multimoney.presentation.navigation.ID_VISA_CARD
import com.multimoney.multimoney.presentation.navigation.SMART_IDS
import com.multimoney.multimoney.presentation.navigation.navgraph.BANK_DETAIL
import com.multimoney.multimoney.presentation.navigation.navgraph.MASKED_CARD
import com.multimoney.multimoney.presentation.navigation.navgraph.PREVIOUS_SCREEN
import com.multimoney.multimoney.presentation.util.catalog.CurrencyType
import com.multimoney.multimoney.presentation.util.workers.startTimedNotification
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

class SmartEditAmountHelper @Inject constructor(
    private val querySmartExchangeRateUseCase: QuerySmartExchangeRateUseCase,
    private val shareHelper: ShareHelper,
    private val savedStateHandle: SavedStateHandle,
    private val dataStorePreferences: DataStorePreferences,
) {

    // stateless
    var idCard: Long = 0
    var identification: String = ""
    var pkUser: String = ""
    var userName: String = ""
    var idCurrency: Int = 0
    var tokenNumber: Long = 0
    var smartAccount: SmartAccountID? = null
    var ibanAccount: IbanAccountID? = null
    var smartCurrency: CurrencyType? = CurrencyType.Dollar
    var ibanCurrency: CurrencyType? = null
    var idBrand: Int = 0
    var shouldDisplayExchange: Boolean = false
    var maskedCardNumber: String = ""
    var bankDetail: String = ""
    var previousScreen: String = ""
    var sheetSubtitle: Int = R.string.smart_payment_amount_bottom_sheet_from_card
    var originIcon: Int = R.drawable.ic_visa_card_item

    suspend fun onStart() {
        idBrand = dataStorePreferences.getIdBrand().first().toInt()
        identification = dataStorePreferences.getIdentification().first()
        pkUser = dataStorePreferences.getPkUser().first()
        userName = dataStorePreferences.getUserName().first()
        smartAccount = savedStateHandle[SMART_IDS]
        smartCurrency = smartAccount?.currencyID?.getCurrencyFromId()
        tokenNumber = smartAccount?.tokenAccount?.toLongOrNull() ?: 0
        idCurrency = smartAccount?.currencyID ?: 0
        previousScreen = savedStateHandle[PREVIOUS_SCREEN] ?: ""

        if (idBrand == Brand.CostaRica.id) {
            initializeCRValues()
        } else {
            initializeSVValues()
        }
    }

    private fun initializeCRValues() {
        ibanAccount = savedStateHandle[IBAN_ACCOUNT]
        ibanCurrency = ibanAccount?.currencyId?.getCurrencyFromId()
        shouldDisplayExchange = smartCurrency != ibanCurrency
        bankDetail = ibanAccount?.bank ?: ""
        maskedCardNumber = ibanAccount?.sinpeAccount ?: ""
        sheetSubtitle = R.string.smart_payment_amount_bottom_sheet_from_card_CR
        originIcon =
            ibanCurrency?.id?.getCurrencyFromId()?.accountIcon ?: CurrencyType.Colon.accountIcon
    }

    private fun initializeSVValues() {
        idCard = savedStateHandle[ID_VISA_CARD] ?: 0
        maskedCardNumber = savedStateHandle[MASKED_CARD] ?: ""
        bankDetail = savedStateHandle[BANK_DETAIL] ?: ""
        sheetSubtitle = R.string.smart_payment_amount_bottom_sheet_from_card
        originIcon = R.drawable.ic_visa_card_item
    }

    suspend fun getSmartExchangeRate(
        user: String = this.userName,
        idBrand: Int = this.idBrand,
        abbreviation: String? = this.ibanCurrency?.disbursementValue,
        identification: String = this.identification,
        idOriginCurrency: String = this.smartCurrency?.id.toString(),
        idDestinationCurrency: String = this.ibanCurrency?.id.toString(),
        currentAmount: Double,
        onSuccess: (ExchangeRateResult?) -> Unit,
        onFailure: () -> Unit,
        onLoading: () -> Unit
    ) {
        querySmartExchangeRateUseCase.invoke(
            user = user,
            idBrand = idBrand,
            abbreviation = abbreviation ?: "",
            identification = identification,
            idOriginCurrency = idOriginCurrency,
            idDestinationCurrency = idDestinationCurrency,
            amount = currentAmount
        ).collectLatest { result ->
            result.onSuccess { rate ->
                onSuccess(rate)
            }
            result.onFailure {
                onFailure()
            }
            result.onLoading {
                onLoading()
            }
        }
    }

    fun onTryLater(
        notificationTitle: String,
        notificationBody: String,
        notificationSmallIcon: Int,
        context: Context,
        navigateBack: () -> Unit
    ) {
        startTimedNotification(
            context,
            notificationTitle,
            notificationBody,
            notificationSmallIcon
        )
        navigateBack()
    }

    fun onShareVoucherImage(
        view: View,
        capturingBounds: Rect
    ) {
        shareHelper.sharedScreenShot(view, capturingBounds)
    }

    companion object {
        const val DEFAULT_DESCRIPTION = "Depósito a cuenta Smart"
        const val ID_NOT_APPLICABLE = -1
        const val NOT_APPLICABLE = "NA"
        const val CURRENCY_SEPARATOR = ','
    }
}