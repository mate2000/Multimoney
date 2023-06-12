package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.SaveCreditOperation
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSaveCreditOperationUseCase {
    suspend operator fun invoke(
        idUserRequest: Long,
        pkUser: Long,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveCreditOperation>>
}
