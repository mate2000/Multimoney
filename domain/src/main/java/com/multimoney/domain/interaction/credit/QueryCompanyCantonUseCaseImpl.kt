package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CompanyCanton
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryCompanyCantonUseCaseImpl(val creditRepository: CreditRepository) : QueryCompanyCantonUseCase {
    override suspend fun invoke(
        pkUser: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<List<CompanyCanton?>?>> = creditRepository.queryCompanyCanton(pkUser, user, idBrand)
}