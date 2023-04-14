package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.ACHAccount
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationACHTransferFavoriteDeleteUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        accountForAchTransferId: Int
    ): Flow<MultimoneyResult<ACHAccount?>>
}
