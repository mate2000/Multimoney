package com.multimoney.domain.interaction.crypto

import androidx.paging.PagingData
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetCryptoCurrencyMovementsUseCaseImpl(val cryptoRepository: CryptoRepository):
    GetCryptoCurrencyMovementsUseCase {

    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        order_time_begin: Any,
        order_time_end: Any,
        pagination_limit: Int
    ): Flow<PagingData<CryptoCurrencyMovement>> {
        return cryptoRepository.getCryptoCurrencyMovements(
            user,
            idBrand,
            identification,
            market,
            order_time_begin,
            order_time_end,
            pagination_limit
        )
    }
}