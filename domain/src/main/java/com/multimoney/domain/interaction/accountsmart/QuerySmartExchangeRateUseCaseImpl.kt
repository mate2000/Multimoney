package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.ExchangeRateResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QuerySmartExchangeRateUseCaseImpl(val repository: SmartAccountRepository) :
    QuerySmartExchangeRateUseCase {
    override suspend fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        abbreviation: String,
        idOriginCurrency: String,
        idDestinationCurrency: String,
        amount: Double
    ): Flow<MultimoneyResult<ExchangeRateResult?>> {
        return repository.querySmartExchangeRate(
            user = user,
            identification = identification,
            idBrand = idBrand,
            abbreviation = abbreviation,
            idOriginCurrency = idOriginCurrency,
            idDestinationCurrency = idDestinationCurrency,
            amount = amount
        )
    }
}