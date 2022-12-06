package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class CryptoCurrencyMovementUseCaseImpl(val repository: CryptoRepository) :
    CryptoCurrencyMovementUseCase {

    override suspend fun invoke(
        user: String?,
        idBrand: Int?,
        identification: String,
        market: String?,
        order_time_begin: Any?,
        order_time_end: Any?,
        pagination_limit: Int?,
        pagination_offset: Int?
    ): Flow<MultimoneyResult<CryptoCurrencyMovement?>> {
        return repository.getCryptoCurrencyMovement(
            user,
            idBrand,
            identification,
            market,
            order_time_begin,
            order_time_end,
            pagination_limit,
            pagination_offset
        )
    }
}