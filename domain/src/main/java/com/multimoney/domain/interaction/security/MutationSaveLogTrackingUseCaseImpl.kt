package com.multimoney.domain.interaction.security

import com.multimoney.domain.model.security.SaveLogTracking
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow

class MutationSaveLogTrackingUseCaseImpl(
    val securityRepository: SecurityRepository
) : MutationSaveLogTrackingUseCase {
    override suspend fun invoke(
        identification: String,
        pkUser: Int,
        keySearch: String,
        data: String,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveLogTracking>> = securityRepository.mutationSaveLogTracking(
        identification,
        pkUser,
        keySearch,
        data,
        idBrand
    )
}
