package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryGetPaymentPointsUseCaseImpl(private val creditRepository: CreditRepository) : QueryGetPaymentPointsUseCase {
    override suspend fun invoke(
        idBrand: Int
    ) = creditRepository.queryGetPaymentPoints(idBrand)
}
