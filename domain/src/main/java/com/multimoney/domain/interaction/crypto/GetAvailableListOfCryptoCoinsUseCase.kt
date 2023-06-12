package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetListOfAvailableCryptoCoins
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetAvailableListOfCryptoCoinsUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        origin: String
    ): Flow<MultimoneyResult<GetListOfAvailableCryptoCoins?>>
}
