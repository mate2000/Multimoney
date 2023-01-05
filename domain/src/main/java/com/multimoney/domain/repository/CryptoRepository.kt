package com.multimoney.domain.repository

import androidx.paging.PagingData
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.CryptoCurrencyNews
import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.crypto.GetHistoricalCurrencyPrices
import com.multimoney.domain.model.crypto.GetListOfAvailableCryptoCoins
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
}