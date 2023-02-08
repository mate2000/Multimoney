package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.BuyCryptoCurrencyData
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface BuyCryptoCurrencyUseCase {
    suspend operator fun invoke(
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
    ): Flow<MultimoneyResult<BuyCryptoCurrencyData>>
}