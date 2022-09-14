package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryHomeDistrictUseCaseImpl(private val creditRepository: CreditRepository) : QueryHomeDistrictUseCase {
    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest: String
    ) = creditRepository.queryHomeDistrict(pkUser, user, idBrand, fkCatalogIdentifier, idUserRequest)
}