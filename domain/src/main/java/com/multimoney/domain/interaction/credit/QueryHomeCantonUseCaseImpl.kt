package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryHomeCantonUseCaseImpl(private val creditRepository: CreditRepository) : QueryHomeCantonUseCase {
    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ) = creditRepository.queryHomeCanton(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
}
