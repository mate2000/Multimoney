package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.LocalTransferFavorite
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryListSavedSACAccountsUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        idClient: Long,
        isFavorite: Boolean
    ): Flow<MultimoneyResult<LocalTransferFavorite?>>
}