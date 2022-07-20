package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidateUserStatus
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryValidateUserStatusUseCaseImpl(val securityRepository: SecurityRepository) :
    QueryValidateUserStatusUseCase {
    override suspend fun invoke(
        pkUser: Int,
        identification: String,
        email: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidateUserStatus?>> =
        securityRepository.queryValidateUserStatus(pkUser, identification, email, idBrand)
}