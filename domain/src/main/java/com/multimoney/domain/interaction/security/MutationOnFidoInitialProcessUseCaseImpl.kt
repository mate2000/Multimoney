package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.OnfidoToken
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow
import java.io.Serializable

class MutationOnFidoInitialProcessUseCaseImpl(val securityRepository: SecurityRepository) :
    MutationOnFidoInitialProcessUseCase, Serializable {
    override suspend fun invoke(
        names: String,
        lastNames: String,
        identification: String,
        applicationId: String,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<OnfidoToken?>> =
        securityRepository.mutationOnFidoInitialProcess(names, lastNames, identification, applicationId, idBrand, user)
}