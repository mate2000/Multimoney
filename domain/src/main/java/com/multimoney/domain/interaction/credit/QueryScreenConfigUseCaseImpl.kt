package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryScreenConfigUseCaseImpl(val creditRepository: CreditRepository) : QueryScreenConfigUseCase {
    override suspend fun invoke(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> =
        creditRepository.queryScreenConfig(pkUser, user, idBrand, idUserRequest)
}