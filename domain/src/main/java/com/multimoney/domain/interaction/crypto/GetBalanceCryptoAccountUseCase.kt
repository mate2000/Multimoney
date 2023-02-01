package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetBalanceCryptoAccountUseCase {

    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
    ): Flow<MultimoneyResult<BalanceCryptoAccount>>
}
