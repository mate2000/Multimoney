package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetTransferFeeData
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetTransferCommissionUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        destinationAddress: String,
        asset: String,
        cryptoNetwork: String,
        amount: Double
    ): Flow<MultimoneyResult<GetTransferFeeData>>
}