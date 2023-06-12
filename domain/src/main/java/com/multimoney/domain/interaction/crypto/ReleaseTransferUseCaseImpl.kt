package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.ReleaseTransactionResponse
import com.multimoney.domain.model.crypto.ValidateDepositAddressResponse
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class ReleaseTransferUseCaseImpl(
    private val cryptoRepository: CryptoRepository
): ReleaseTransferUseCase {

    override suspend fun invoke(
        identification: String,
        user: String,
        market: String,
        senderFullName: String,
        reason: String,
        platform: String,
        idTransaction: String
    ): Flow<MultimoneyResult<ReleaseTransactionResponse>> {
        return cryptoRepository.releaseCryptoTransfer(
            identification,
            user,
            market,
            senderFullName,
            reason,
            platform,
            idTransaction
        )
    }
}
