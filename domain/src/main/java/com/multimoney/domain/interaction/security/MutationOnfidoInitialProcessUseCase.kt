package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationOnfidoInitialProcessUseCase {
    suspend operator fun invoke(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<OnfidoToken?>>
}