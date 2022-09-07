package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.UserData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryValidateUserExistsUseCaseImpl(val securityRepository: SecurityRepository) : QueryValidateUserExistsUseCase {
    override suspend fun invoke(
        email: String
    ): Flow<MultimoneyResult<UserData?>> = securityRepository.queryValidateUserExists(
        email
    )
}