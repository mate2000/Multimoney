package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.SendCryptoToAddressData
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SendCryptoToAddressUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        identification: String,
        destinationAddress: String,
        feeId: String,
        asset: String,
        market: String,
        cryptoNetwork: String,
        amount: Double,
        fee: Double,
        internalFee: Double,
        taxAmount: Double,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SendCryptoToAddressData>>
}