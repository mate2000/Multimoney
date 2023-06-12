package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationInitialUseCaseImpl(val repository: SmartAccountRepository) :
    MutationInitialRequestUseCase {
    override suspend fun invoke(
        pkUser: Long,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<GlobalRequest?>> =
        repository.mutationInitialRequestSmartAccount(pkUser, idBrand, user)
}