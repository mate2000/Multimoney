package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QueryEmploymentSituationUseCaseImpl @Inject constructor(private val creditRepository: CreditRepository) : QueryEmploymentSituationUseCase {
    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> =
        creditRepository.queryEmploymentSituation(
            pkUser = pkUser,
            user = user,
            idBrand = idBrand,
            idUserRequest = idUserRequest
        )
}