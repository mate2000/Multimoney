package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationACHTransferFavoriteDeleteUseCaseImpl(
    val repository: SmartAccountRepository
) : MutationACHTransferFavoriteDeleteUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        accountForAchTransferId: Int
    ): Flow<MultimoneyResult<ACHAccount?>> = repository.mutationACHTransferFavoriteDelete(
        user = user,
        idBrand = idBrand,
        accountForAchTransferId = accountForAchTransferId
    )
}
