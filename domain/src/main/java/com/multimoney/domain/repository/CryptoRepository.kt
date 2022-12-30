package com.multimoney.domain.repository

import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
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

    suspend fun getCryptoCurrencyMovements(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        startDate: String,
        endDate: String
    ) : Flow<MultimoneyResult<CryptoCurrencyMovement?>>

    suspend fun getCryptoCurrencyHistory(
        market: String,
        user: String,
        idBrand: Int,
        startDate: String,
        endDate: String,
        maxPoints: Long,
        paginationLimit: Int,
        paginationOffset: Int,
    ) : Flow<MultimoneyResult<GetHistoricalClientBalance?>>
}