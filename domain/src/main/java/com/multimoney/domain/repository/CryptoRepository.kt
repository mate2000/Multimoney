package com.multimoney.domain.repository

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
}