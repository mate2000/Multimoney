package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetCryptoReceiveAddressData
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface GetCryptoReceiveAddressUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        asset: String,
        crypto_network: String
    ): Flow<MultimoneyResult<GetCryptoReceiveAddressData>>
}