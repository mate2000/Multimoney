package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.StepByStep
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryStepByStepUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        idRequest: Long
    ): Flow<MultimoneyResult<StepByStep?>>
}
