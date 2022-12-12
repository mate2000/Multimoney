package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.AccountStatement
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QueryAccountStatementUseCaseImpl @Inject constructor(val creditRepository: CreditRepository) :
    QueryAccountStatementUseCase {
    override suspend fun invoke(
        creditNumber: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<AccountStatement?>> =
        creditRepository.queryAccountStatement(creditNumber = creditNumber, user = user, idBrand = idBrand)
}
