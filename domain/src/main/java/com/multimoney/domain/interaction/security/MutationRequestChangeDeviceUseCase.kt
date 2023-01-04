package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.RequestChangeDevice
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationRequestChangeDeviceUseCase {
    suspend operator fun invoke(
        email: String
    ): Flow<MultimoneyResult<RequestChangeDevice>>
}
