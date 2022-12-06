package com.multimoney.domain.repository

import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {

    suspend fun getCryptoCurrencyMovement(
        user: String?,
        idBrand: Int?,
        identification: String,
        market: String?,
        order_time_begin: Any?,
        order_time_end: Any?,
        pagination_limit: Int? = 100,
        pagination_offset: Int? = 0,
    ): Flow<MultimoneyResult<CryptoCurrencyMovement?>>

    suspend fun getHistoricalClientBalance(
        user: String?,
        idBrand: Int?,
        identification: String,
        baseAsset: String,
        startDate: String,
        endDate: String
    ): Flow<MultimoneyResult<GetHistoricalClientBalance?>>
}