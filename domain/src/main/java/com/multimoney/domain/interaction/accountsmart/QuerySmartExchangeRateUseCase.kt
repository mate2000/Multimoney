package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QuerySmartExchangeRateUseCase {
    suspend operator fun invoke(
        user: String,
        identification: String,
        idOriginCurrency: String,
        idDestinationCurrency: String
    ): Flow<MultimoneyResult<Double?>>
}