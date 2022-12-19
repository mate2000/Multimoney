package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QuerySmartExchangeRateUseCaseImpl(val repository: SmartAccountRepository) :
    QuerySmartExchangeRateUseCase {
    override suspend fun invoke(
        user: String,
        identification: String,
        idOriginCurrency: String,
        idDestinationCurrency: String
    ): Flow<MultimoneyResult<Double?>> {
        return repository.querySmartExchangeRate(
            user = user,
            identification = identification,
            idOriginCurrency = idOriginCurrency,
            idDestinationCurrency = idDestinationCurrency
        )
    }
}