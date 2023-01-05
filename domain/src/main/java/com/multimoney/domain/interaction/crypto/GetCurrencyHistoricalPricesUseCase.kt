package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetHistoricalCurrencyPrices
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetCurrencyHistoricalPricesUseCase {

    suspend operator fun invoke(
        market: String,
        max_data_points: Long,
        range_begin: String,
        range_end: String,
        pagination_limit: Int,
        pagination_offset: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<GetHistoricalCurrencyPrices>>
}