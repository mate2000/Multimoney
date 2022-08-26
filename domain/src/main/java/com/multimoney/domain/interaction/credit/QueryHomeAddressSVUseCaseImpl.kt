package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.HomeAddress
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryHomeAddressSVUseCaseImpl(val creditRepository: CreditRepository) : QueryHomeAddressSVUseCase {
    override suspend fun invoke(pkUser: String, user: String, idBrand: Int): Flow<MultimoneyResult<HomeAddress?>> =
        creditRepository.queryHomeAddressSV(pkUser, user, idBrand)
}