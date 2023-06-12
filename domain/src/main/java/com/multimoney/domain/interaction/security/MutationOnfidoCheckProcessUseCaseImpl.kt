package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.OnfidoCheckProcess
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationOnfidoCheckProcessUseCaseImpl(
    val securityRepository: SecurityRepository
) : MutationOnfidoCheckProcessUseCase {
    override suspend fun invoke(
        identification: String,
        applicantId: String,
        currentFlow: String,
        pkUser: Long,
        userRequestId: Long,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<OnfidoCheckProcess>> = securityRepository.mutationOnFidoCheckProcess(
        identification,
        applicantId,
        currentFlow,
        pkUser,
        userRequestId,
        idBrand,
        user
    )
}
