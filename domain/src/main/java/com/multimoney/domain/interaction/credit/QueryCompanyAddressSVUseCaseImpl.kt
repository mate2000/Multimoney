package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CompanyAddress
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryCompanyAddressSVUseCaseImpl(val creditRepository: CreditRepository) : QueryCompanyAddressSVUseCase {
    override suspend fun invoke(pkUser: String, user: String, idBrand: Int): Flow<MultimoneyResult<CompanyAddress?>> =
        creditRepository.queryCompanyAddressSV(pkUser, user, idBrand)
}