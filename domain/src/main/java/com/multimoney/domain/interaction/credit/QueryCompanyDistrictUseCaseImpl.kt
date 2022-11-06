package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryCompanyDistrictUseCaseImpl(private val creditRepository: CreditRepository) : QueryCompanyDistrictUseCase {
    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: Int
    ) = creditRepository.queryCompanyDistrict(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
}
