package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.GetHistoricalClientBalance
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetCryptoCurrencyMovementsUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        startDate: String,
        endDate: String
    ): Flow<MultimoneyResult<CryptoCurrencyMovement?>>
}