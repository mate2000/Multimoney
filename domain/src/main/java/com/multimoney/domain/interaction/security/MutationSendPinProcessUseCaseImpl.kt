package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.SendPinResponse
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationSendPinProcessUseCaseImpl(val securityRepository: SecurityRepository) : MutationSendPinProcessUseCase {
    override suspend fun invoke(
        identification: String,
        firstName: String,
        email: String,
        cellPhone: String,
        sendMethod: String,
        pkUser: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SendPinResponse?>> = securityRepository.mutationSendPinProcess(
        identification,
        firstName,
        email,
        cellPhone,
        sendMethod,
        pkUser,
        idBrand,
        user
    )
}