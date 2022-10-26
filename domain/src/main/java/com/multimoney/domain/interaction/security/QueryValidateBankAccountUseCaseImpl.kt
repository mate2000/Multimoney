package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidateAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryValidateBankAccountUseCaseImpl(val repository: SecurityRepository) :
    QueryValidateBankAccountUseCase {
    override suspend fun invoke(
        account: String,
        identification: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateAccount?>> = repository.queryValidateBankAccount(
        account, identification, user, idBrand
    )
}