package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.ReleaseTransactionResponse
import com.multimoney.domain.model.crypto.ValidateDepositAddressResponse
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface ReleaseTransferUseCase {

    suspend operator fun invoke(
        identification: String,
        user: String,
        market: String,
        senderFullName: String,
        reason: String,
        platform: String,
        idTransaction: String
    ): Flow<MultimoneyResult<ReleaseTransactionResponse>>
}
