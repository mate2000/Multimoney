package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CompanyDistrict
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryCompanyDistrictUseCaseImpl(val creditRepository: CreditRepository) : QueryCompanyDistrictUseCase {
    override suspend fun invoke(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<List<CompanyDistrict?>?>> = creditRepository.queryCompanyDistrict(pkUser, user, idBrand)
}