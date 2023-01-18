package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.BuyCryptoCurrencyOrder
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class BuyCryptoCurrencyUseCaseImpl(
    val repository: CryptoRepository
) : BuyCryptoCurrencyUseCase {
    override suspend fun invoke(
        pkUser: Int,
        identification: String,
        market: String,
        orderAmount: Double,
        commissionAmount: Double,
        taxAmount: Double,
        accountToken: Long,
        exchangeRate: Double,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<BuyCryptoCurrencyOrder>> {
        return repository.mutationBuyCryptoCurrency(
            pkUser,
            identification,
            market,
            orderAmount,
            commissionAmount,
            taxAmount,
            accountToken,
            exchangeRate,
            idBrand,
            user
        )
    }
}