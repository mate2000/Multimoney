package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QueryEmissionPlaceUseCaseImpl @Inject constructor(private val creditRepository: CreditRepository) :
    QueryEmissionPlaceUseCase {
    override suspend fun invoke(
        pkUser: Int,
        idUserRequest: Int,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>> =
        creditRepository.queryEmissionPlace(
            pkUser = pkUser,
            idUserRequest = idUserRequest,
            idBrand = idBrand,
            user = user
        )
}