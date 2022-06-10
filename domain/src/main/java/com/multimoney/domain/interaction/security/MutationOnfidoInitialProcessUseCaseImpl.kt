package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationOnfidoInitialProcessUseCaseImpl(val securityRepository: SecurityRepository) :
    MutationOnfidoInitialProcessUseCase {
    override suspend fun invoke(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<OnfidoToken?>> =
        securityRepository.mutationOnfidoInitialProcess(names, lastNames, identification, applicationId, idBrand, user)
}