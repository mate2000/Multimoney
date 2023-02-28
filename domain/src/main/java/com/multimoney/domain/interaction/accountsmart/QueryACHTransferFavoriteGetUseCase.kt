package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.ACHAccountFull
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryACHTransferFavoriteGetUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        achTransferId: Int
    ): Flow<MultimoneyResult<ACHAccountFull?>>
}
