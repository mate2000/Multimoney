package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.DeleteCard
import kotlinx.coroutines.flow.Flow

interface MutationDeleteCardVDUseCase {
    suspend operator fun invoke(
        identification: String,
        user: String,
        idBrand: Int,
        idCard: Long
    ): Flow<MultimoneyResult<DeleteCard?>>
}