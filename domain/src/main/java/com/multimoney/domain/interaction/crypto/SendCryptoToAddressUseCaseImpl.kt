package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.SendCryptoToAddressData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class SendCryptoToAddressUseCaseImpl(
    val repository: CryptoRepository
): SendCryptoToAddressUseCase {
    override suspend fun invoke(
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
    ): Flow<MultimoneyResult<SendCryptoToAddressData>> =
        repository.sendCryptoToAddress(
            pkUser,
            identification,
            destinationAddress,
            feeId,
            asset,
            market,
            cryptoNetwork,
            amount,
            fee,
            internalFee,
            taxAmount,
            idBrand,
            user
        )
}