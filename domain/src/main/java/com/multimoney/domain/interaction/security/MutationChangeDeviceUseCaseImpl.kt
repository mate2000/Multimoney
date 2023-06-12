package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.ChangeDevice
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationChangeDeviceUseCaseImpl(
    val securityRepository: SecurityRepository
) : MutationChangeDeviceUseCase {
    override suspend fun invoke(
        email: String,
        otp: String
    ): Flow<MultimoneyResult<ChangeDevice>> = securityRepository.mutationChangeDevice(
        email,
        otp
    )
}