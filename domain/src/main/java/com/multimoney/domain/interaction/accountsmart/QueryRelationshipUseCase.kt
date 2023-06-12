package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.RelationshipData
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryRelationshipUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        idRequest: Int,
    ): Flow<MultimoneyResult<RelationshipData?>>
}