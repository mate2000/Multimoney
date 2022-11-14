package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryOccupationUseCaseImpl(private val creditRepository: CreditRepository) : QueryOccupationUseCase {
    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ) = creditRepository.queryOccupation(pkUser, user, idBrand, idUserRequest)
}
