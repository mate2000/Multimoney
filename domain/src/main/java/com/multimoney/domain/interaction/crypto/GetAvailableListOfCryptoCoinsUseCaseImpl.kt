package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetListOfAvailableCryptoCoins
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetAvailableListOfCryptoCoinsUseCaseImpl(val repository: CryptoRepository)
    : GetAvailableListOfCryptoCoinsUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        origin: String
    ): Flow<MultimoneyResult<GetListOfAvailableCryptoCoins?>> {
        return repository.getAvailableListOfCryptoCoins(user, idBrand, origin)
    }
}
