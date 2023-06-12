package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.RequestChangeDevice
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationRequestChangeDeviceUseCaseImpl(
    val securityRepository: SecurityRepository
) : MutationRequestChangeDeviceUseCase {
    override suspend fun invoke(
        email: String
    ): Flow<MultimoneyResult<RequestChangeDevice>> = securityRepository.mutationRequestChangeDevice(
        email
    )
}