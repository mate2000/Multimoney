package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetHistoricalClientBalanceUseCaseImpl(val repository: CryptoRepository) :
    GetHistoricalClientBalanceUseCase {

    override suspend fun invoke(
        user: String?,
        idBrand: Int?,
        identification: String,
        baseAsset: String,
        startDate: String,
        endDate: String
    ): Flow<MultimoneyResult<GetHistoricalClientBalance?>> {
        return repository.getHistoricalClientBalance(
            user,
            idBrand,
            identification,
            baseAsset,
            startDate,
            endDate
        )
    }

}