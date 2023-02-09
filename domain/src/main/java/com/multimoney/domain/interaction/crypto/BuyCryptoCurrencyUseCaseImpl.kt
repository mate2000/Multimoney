package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.BuyCryptoCurrencyData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class BuyCryptoCurrencyUseCaseImpl(
    val repository: CryptoRepository
): BuyCryptoCurrencyUseCase {
    override suspend fun invoke(
        pkUser: Int,
        identification: String,
        market: String,
        commissionAmount: Double,
        taxAmount: Double,
        accountToken: Long,
        exchangeRate: Double,
        idBrand: Int,
        user: String,
        quoteId: String,
        quoteAmount: Double,
        fee: Double,
        internalFee: Double,
        totalFee: Double
    ): Flow<MultimoneyResult<BuyCryptoCurrencyData>> =
        repository.buyCryptoCurrency(
            pkUser,
            identification,
            market,
            commissionAmount,
            taxAmount,
            accountToken,
            exchangeRate,
            idBrand,
            user,
            quoteId,
            quoteAmount,
            fee,
            internalFee,
            totalFee
        )
}