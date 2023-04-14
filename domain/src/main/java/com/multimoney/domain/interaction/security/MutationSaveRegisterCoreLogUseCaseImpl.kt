package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.SaveRegisterCoreLog
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationSaveRegisterCoreLogUseCaseImpl(
    val repository: SecurityRepository
) : MutationSaveRegisterCoreLogUseCase {
    override suspend fun invoke(
        user: String?,
        idBrand: Int,
        process: String,
        parameters: String,
        result: String
    ): Flow<MultimoneyResult<SaveRegisterCoreLog>> = repository.mutationSaveRegisterCoreLog(
        user,
        idBrand,
        process,
        parameters,
        result
    )
}