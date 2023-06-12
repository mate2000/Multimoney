package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.Company
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryCompanyNameByIdentityUseCaseImpl(private val securityRepository: SecurityRepository) :
    QueryCompanyNameByIdentityUseCase {
    override suspend fun invoke(
        identification: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<Company?>> = securityRepository.queryGetCompanyNameByIdentification(identification, idBrand, user)
}
