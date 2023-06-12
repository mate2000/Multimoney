package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.Token
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryGetTokenUseCaseImpl(
    val securityRepository: SecurityRepository
) : QueryGetTokenUseCase {
    override suspend fun invoke(): Flow<MultimoneyResult<Token>> = securityRepository.queryGetToken()
}
