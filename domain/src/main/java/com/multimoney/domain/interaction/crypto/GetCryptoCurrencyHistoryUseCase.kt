package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetCryptoCurrencyHistoryUseCase {
    suspend operator fun invoke(
        market: String,
        user: String,
        idBrand: Int,
        startDate: String,
        endDate: String,
        maxPoints: Long,
        paginationLimit: Int,
        paginationOffset: Int
    ): Flow<MultimoneyResult<GetHistoricalClientBalance?>>
}