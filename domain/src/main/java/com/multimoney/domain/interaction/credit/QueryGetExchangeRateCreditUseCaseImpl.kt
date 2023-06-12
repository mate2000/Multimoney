package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryGetExchangeRateCreditUseCaseImpl(val creditRepository: CreditRepository) :
    QueryGetExchangeRateCreditUseCase {
    override suspend fun invoke(
        idBrand: Int,
        user: String,
        identification: String,
        idOriginCurrency: String,
        idDestinationCurrency: String,
        amount: Double
    ) = creditRepository.queryGetExchangeRateCredit(
        idBrand,
        user,
        identification,
        idOriginCurrency,
        idDestinationCurrency,
        amount
    )
}
