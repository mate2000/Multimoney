package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.LocalTransferFavorite
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryListSavedSACAccountsUseCaseImpl(
    val repository: SmartAccountRepository
) : QueryListSavedSACAccountsUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idClient: Long,
        isFavorite: Boolean
    ): Flow<MultimoneyResult<LocalTransferFavorite?>> =
        repository.querySavedSACAccountsSmart(
            idBrand,
            user,
            isFavorite,
            idClient
        )
}