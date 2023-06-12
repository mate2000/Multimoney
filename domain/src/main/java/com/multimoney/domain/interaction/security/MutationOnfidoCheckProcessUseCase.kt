package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.OnfidoCheckProcess
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationOnfidoCheckProcessUseCase {
    suspend operator fun invoke(
        identification: String,
        applicantId: String,
        currentFlow: String,
        pkUser: Long,
        userRequestId: Long,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<OnfidoCheckProcess>>
}
