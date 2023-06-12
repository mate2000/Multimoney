package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.ACHAccountFull
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryACHTransferFavoriteGetUseCaseImpl(
    private val smartAccountRepository: SmartAccountRepository
) : QueryACHTransferFavoriteGetUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        achTransferId: Int
    ): Flow<MultimoneyResult<ACHAccountFull?>> = smartAccountRepository.queryACHTransferFavoriteGet(
        user,
        idBrand,
        achTransferId
    )
}
