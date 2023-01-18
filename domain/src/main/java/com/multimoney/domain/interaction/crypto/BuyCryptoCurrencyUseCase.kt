package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.BuyCryptoCurrencyOrder
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface BuyCryptoCurrencyUseCase {
    suspend operator fun invoke(
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
    ): Flow<MultimoneyResult<BuyCryptoCurrencyOrder>>
}