package com.multimoney.domain.interaction.crypto

import androidx.paging.PagingData
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import kotlinx.coroutines.flow.Flow

interface GetCryptoCurrencyMovementsUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        order_time_begin: Any,
        order_time_end: Any,
        pagination_limit: Int
    ): Flow<PagingData<CryptoCurrencyMovement>>
}
