package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.SellCryptoCurrencyHQRData
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SellCryptoCurrencyUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        identification: String,
        market: String,
        commissionPercentage: Double,
        taxPercentage: Double,
        accountToken: Double,
        exchangeRate: Double,
        idBrand: Int,
        user: String,
        quoteId: String,
        baseAmount: Double,
        fee: Double,
        internalFee: Double,
        totalFee: Double
    ): Flow<MultimoneyResult<SellCryptoCurrencyHQRData>>
}