package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.RelationshipData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryRelationshipUseCaseImpl(val repository: SmartAccountRepository): QueryRelationshipUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idRequest: Int,
    ): Flow<MultimoneyResult<RelationshipData?>> {
        return repository.queryRelationship(user, idBrand, idRequest)
    }
}