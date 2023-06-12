package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.PricesQuoteAndCommissionData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetPriceQuoteAndCommissionUseCaseImpl(
    val repository: CryptoRepository
) : GetPriceQuoteAndCommissionsUseCase {
    override suspend fun invoke(
        asset: String,
        crypto_network: String,
        idBrand: Int,
        user: String,
        market: String,
        identification: String,
        quote_amount: Double,
        base_amount: Double,
        side: String
    ): Flow<MultimoneyResult<PricesQuoteAndCommissionData>> =
        repository.getPriceQuoteAndCommission(
            asset,
            crypto_network,
            idBrand,
            user,
            market,
            identification,
            quote_amount,
            base_amount,
            side
        )
}