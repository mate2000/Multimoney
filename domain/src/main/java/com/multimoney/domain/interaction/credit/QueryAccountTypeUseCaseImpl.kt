package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.RegularExpressionList
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryAccountTypeUseCaseImpl(val repository: CreditRepository) : QueryAccountTypeUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<RegularExpressionList?>> =
        repository.queryBankAccountType(user, idBrand)
}