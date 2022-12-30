package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetCryptoPriceHistoryUseCaseImpl(val repository: CryptoRepository) :
    GetCryptoCurrencyHistoryUseCase {

    override suspend fun invoke(
        market: String,
        user: String,
        idBrand: Int,
        startDate: String,
        endDate: String,
        maxPoints: Long,
        paginationLimit: Int,
        paginationOffset: Int
    ): Flow<MultimoneyResult<GetHistoricalClientBalance?>> {
        return repository.getCryptoCurrencyHistory(
            market,
            user,
            idBrand,
            startDate,
            endDate,
            maxPoints,
            paginationLimit,
            paginationOffset
        )
    }

}