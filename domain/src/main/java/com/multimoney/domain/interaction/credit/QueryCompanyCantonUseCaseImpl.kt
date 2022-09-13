package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryCompanyCantonUseCaseImpl(private val creditRepository: CreditRepository) : QueryCompanyCantonUseCase {
    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String
    ) = creditRepository.queryCompanyCanton(pkUser, user, idBrand, fkCatalogIdentifier)
}