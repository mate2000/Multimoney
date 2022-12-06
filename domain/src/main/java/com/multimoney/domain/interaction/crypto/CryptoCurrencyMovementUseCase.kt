package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface CryptoCurrencyMovementUseCase {
    suspend operator fun invoke(
        user: String?,
        idBrand: Int?,
        identification: String,
        market: String?,
        order_time_begin: Any?,
        order_time_end: Any?,
        pagination_limit: Int?,
        pagination_offset: Int?
    ): Flow<MultimoneyResult<CryptoCurrencyMovement?>>
}