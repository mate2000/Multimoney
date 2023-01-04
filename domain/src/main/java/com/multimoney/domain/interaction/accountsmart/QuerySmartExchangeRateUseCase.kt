package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.ExchangeRateResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QuerySmartExchangeRateUseCase {
    suspend operator fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        abbreviation: String,
        idOriginCurrency: String,
        idDestinationCurrency: String,
        amount: Double
    ): Flow<MultimoneyResult<ExchangeRateResult?>>
}