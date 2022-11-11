package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryProfessionsUseCaseImpl(private val creditRepository: CreditRepository) : QueryProfessionsUseCase {
    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ) = creditRepository.queryProfession(pkUser, user, idBrand, idUserRequest)
}
