package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.ExchangeRate
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetExchangeRateCreditUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        user: String,
        identification: String,
        idOriginCurrency: String,
        idDestinationCurrency: String,
        amount: Double
    ): Flow<MultimoneyResult<ExchangeRate?>>
}
