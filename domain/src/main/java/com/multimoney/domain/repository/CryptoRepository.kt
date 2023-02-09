package com.multimoney.domain.repository

import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import androidx.paging.PagingData
import com.multimoney.domain.model.crypto.BuyCryptoCurrencyData
import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.domain.model.crypto.CryptoCurrencyNews
import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.crypto.GetHistoricalCurrencyPrices
import com.multimoney.domain.model.crypto.GetListOfAvailableCryptoCoins
import com.multimoney.domain.model.crypto.PricesQuoteAndCommissionData
import com.multimoney.domain.model.crypto.SendCryptoToAddressData
import com.multimoney.domain.model.crypto.SendCryptoToAddressResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {

    suspend fun getHistoricalClientBalance(
        user: String,
        idBrand: Int,
        identification: String,
        baseAsset: String,
        startDate: String,
        endDate: String
    ): Flow<MultimoneyResult<GetHistoricalClientBalance?>>

    suspend fun getAvailableListOfCryptoCoins(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<GetListOfAvailableCryptoCoins?>>

    suspend fun getCryptoCurrencyMovements(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        order_time_begin: Any,
        order_time_end: Any,
        pagination_limit: Int
    ): Flow<PagingData<CryptoCurrencyMovement>>

    suspend fun getCurrencyHistoricalPrices(
        market: String,
        max_data_points: Long,
        range_begin: String,
        range_end: String,
        pagination_limit: Int,
        pagination_offset: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<GetHistoricalCurrencyPrices>>

    suspend fun getCurrencyNews(
        baseAsset: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CryptoCurrencyNews>>

    suspend fun getPriceQuoteAndCommission(
        asset: String,
        crypto_network: String,
        idBrand: Int,
        user: String,
        market: String,
        identification: String,
        quote_amount: Double,
        base_amount: Double,
        side: String
    ): Flow<MultimoneyResult<PricesQuoteAndCommissionData>>

    suspend fun buyCryptoCurrency(
        pkUser: Int,
        identification: String,
        market: String,
        commissionAmount: Double,
        taxAmount: Double,
        accountToken: Long,
        exchangeRate: Double,
        idBrand: Int,
        user: String,
        quoteId: String,
        quoteAmount: Double,
        fee: Double,
        internalFee: Double,
        totalFee: Double
    ): Flow<MultimoneyResult<BuyCryptoCurrencyData>>

    suspend fun getBalanceCryptoAccount(
        user: String,
        idBrand: Int,
        identification: String
    ): Flow<MultimoneyResult<BalanceCryptoAccount>>

    suspend fun sendCryptoToAddress(
        pkUser: Int,
        identification: String,
        destinationAddress: String,
        feeId: String,
        asset: String,
        market: String,
        cryptoNetwork: String,
        amount: Double,
        fee: Double,
        internalFee: Double,
        taxAmount: Double,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SendCryptoToAddressData>>
}
