package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryCompanyProvinceUseCaseImpl(private val creditRepository: CreditRepository) : QueryCompanyProvinceUseCase {
    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ) = creditRepository.queryCompanyProvince(pkUser, user, idBrand, idUserRequest)
}
