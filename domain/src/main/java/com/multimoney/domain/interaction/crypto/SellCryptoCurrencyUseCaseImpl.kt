package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.repository.CryptoRepository

class SellCryptoCurrencyUseCaseImpl(
    val repository: CryptoRepository
): SellCryptoCurrencyUseCase {
    override suspend fun invoke(
        pkUser: Int,
        identification: String,
        market: String,
        commissionPercentage: Double,
        taxPercentage: Double,
        accountToken: Long,
        exchangeRate: Double,
        idBrand: Int,
        user: String,
        quoteId: String,
        baseAmount: Double,
        fee: Double,
        internalFee: Double,
        totalFee: Double
    ) = repository.sellCryptoCurrency(
        pkUser,
        identification,
        market,
        commissionPercentage,
        taxPercentage,
        accountToken,
        exchangeRate,
        idBrand,
        user,
        quoteId,
        baseAmount,
        fee,
        internalFee,
        totalFee
    )
}