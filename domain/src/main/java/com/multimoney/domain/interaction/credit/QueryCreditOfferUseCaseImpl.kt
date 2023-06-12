package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryCreditOfferUseCaseImpl(private val creditRepository: CreditRepository) : QueryCreditOfferUseCase {
    override suspend fun invoke(pkUser: Int, idBrand: Int) = creditRepository.queryCreditOffer(pkUser, idBrand)
}