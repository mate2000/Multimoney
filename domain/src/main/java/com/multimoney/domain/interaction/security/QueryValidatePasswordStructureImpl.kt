package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ValidatePasswordStructure
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class QueryValidatePasswordStructureImpl(val securityRepository: SecurityRepository) :
    QueryValidatePasswordStructure {

    override suspend fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<ValidatePasswordStructure>> =
        securityRepository.queryValidatePasswordStructure(pkUser, user, idBrand)
}