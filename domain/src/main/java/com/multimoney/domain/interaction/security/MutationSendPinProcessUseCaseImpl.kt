package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.SendPinProcess
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
        user: String,
        flowOrigin: Int
    ): Flow<MultimoneyResult<SendPinProcess?>> = securityRepository.mutationSendPinProcess(
        identification,
        firstName,
        email,
        cellPhone,
        sendMethod,
        pkUser,
        idBrand,
        user,
        flowOrigin
    )
}