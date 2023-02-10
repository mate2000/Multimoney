package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetTransferFeeData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetTransferCommissionUseCaseImpl(
    private val cryptoRepository: CryptoRepository
) : GetTransferCommissionUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        destinationAddress: String,
        asset: String,
        cryptoNetwork: String,
        amount: Double
    ): Flow<MultimoneyResult<GetTransferFeeData>> {
        return cryptoRepository.getTransferCommission(
            user,
            idBrand,
            destinationAddress,
            asset,
            cryptoNetwork,
            amount
        )
    }
}