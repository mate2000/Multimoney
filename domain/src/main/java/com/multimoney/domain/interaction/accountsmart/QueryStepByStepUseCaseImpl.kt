package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.StepByStep
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryStepByStepUseCaseImpl(val repository: SmartAccountRepository) : QueryStepByStepUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idRequest: Int,
    ): Flow<MultimoneyResult<StepByStep?>> = repository.queryStepByStep(user, idBrand, idRequest)
}