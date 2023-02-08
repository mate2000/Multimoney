package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetHistoricalCurrencyPrices
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetCurrencyHistoricalPricesUseCaseImpl(val repository: CryptoRepository)
    : GetCurrencyHistoricalPricesUseCase {

    override suspend fun invoke(
        market: String,
        max_data_points: Long,
        range_begin: String,
        range_end: String,
        pagination_limit: Int,
        pagination_offset: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<GetHistoricalCurrencyPrices>> {
        return repository.getCurrencyHistoricalPrices(
            market,
            max_data_points,
            range_begin,
            range_end,
            pagination_limit,
            pagination_offset,
            user,
            idBrand
        )
    }
}