package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.BanksAndRegularExpression
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QueryBanksAndRegularExpressionUseCaseImpl @Inject constructor(val creditRepository: CreditRepository) :
    QueryBanksAndRegularExpressionUseCase {
    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): Flow<MultimoneyResult<BanksAndRegularExpression>> =
        creditRepository.queryBanksAndRegularExpression(pkUser, user, idBrand, idUserRequest)
}