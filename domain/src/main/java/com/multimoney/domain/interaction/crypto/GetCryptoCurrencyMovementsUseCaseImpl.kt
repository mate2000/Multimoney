package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetCryptoCurrencyMovementsUseCaseImpl(val repository: CryptoRepository) :
    GetCryptoCurrencyMovementsUseCase {

    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        startDate: String,
        endDate: String
    ): Flow<MultimoneyResult<CryptoCurrencyMovement?>> {
        return repository.getCryptoCurrencyMovements(
            user,
            idBrand,
            identification,
            market,
            startDate,
            endDate
        )
    }

}