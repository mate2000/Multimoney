package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.SaveCreditOperation
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class MutationSaveCreditOperationUseCaseImpl(val creditRepository: CreditRepository) : MutationSaveCreditOperationUseCase {
    override suspend fun invoke(
        idUserRequest: Long,
        pkUser: Long,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveCreditOperation>> = creditRepository.mutationSaveCreditOperation(
        idUserRequest,
        pkUser,
        user,
        idBrand
    )
}
