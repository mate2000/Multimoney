package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetHistoricalClientBalanceUseCase {
    suspend operator fun invoke(
        user: String?,
        idBrand: Int?,
        identification: String,
        baseAsset: String,
        startDate: String,
        endDate: String
    ): Flow<MultimoneyResult<GetHistoricalClientBalance?>>
}