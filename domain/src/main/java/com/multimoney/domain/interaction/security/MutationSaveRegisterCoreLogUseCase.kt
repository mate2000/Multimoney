package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.SaveRegisterCoreLog
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSaveRegisterCoreLogUseCase {
    suspend operator fun invoke(
        user: String?,
        idBrand: Int,
        process: String,
        parameters: String,
        result: String
    ): Flow<MultimoneyResult<SaveRegisterCoreLog>>
}