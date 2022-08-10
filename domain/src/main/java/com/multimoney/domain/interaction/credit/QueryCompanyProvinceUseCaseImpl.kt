package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CompanyProvince
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryCompanyProvinceUseCaseImpl(private val creditRepository: CreditRepository) : QueryCompanyProvinceUseCase {
    override suspend fun invoke(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<List<CompanyProvince?>?>> = creditRepository.queryCompanyProvince(pkUser, user, idBrand)
}