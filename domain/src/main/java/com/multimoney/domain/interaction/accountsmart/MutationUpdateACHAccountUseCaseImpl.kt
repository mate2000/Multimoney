package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationUpdateACHAccountUseCaseImpl(
    val repository: SmartAccountRepository
) : MutationUpdateACHAccountUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        description: String,
        accountId: Int
    ): Flow<MultimoneyResult<Int?>> = repository.mutationUpdateACHAccount(
        user = user,
        idBrand = idBrand,
        description = description,
        achTransferId = accountId
    )
}