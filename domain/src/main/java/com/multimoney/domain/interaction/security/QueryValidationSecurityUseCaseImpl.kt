package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidateSecurity
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryValidationSecurityUseCaseImpl(val securityRepository: SecurityRepository) : QueryValidationSecurityUseCase {
    override suspend fun invoke(
        pkUser: String,
        password: String,
        user: String,
        idBrand: Int,
        actionSecurity: Int
    ): Flow<MultimoneyResult<ValidateSecurity?>> = securityRepository.queryValidationSecurity(
        pkUser, password, user, idBrand, actionSecurity
    )
}