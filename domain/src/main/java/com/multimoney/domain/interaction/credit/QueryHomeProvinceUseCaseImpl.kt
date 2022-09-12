package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryHomeProvinceUseCaseImpl(private val creditRepository: CreditRepository) : QueryHomeProvinceUseCase {
    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest:String
    ) = creditRepository.queryHomeProvince(pkUser, user, idBrand, idUserRequest)
}