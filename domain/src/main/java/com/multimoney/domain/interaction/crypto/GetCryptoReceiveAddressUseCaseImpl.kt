package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetCryptoReceiveAddressData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetCryptoReceiveAddressUseCaseImpl(
    private val cryptoRepository: CryptoRepository
) : GetCryptoReceiveAddressUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        asset: String,
        crypto_network: String
    ): Flow<MultimoneyResult<GetCryptoReceiveAddressData>> {
        return cryptoRepository.getCryptoCurrencyReceiveAddress(
            user,
            idBrand,
            identification,
            asset,
            crypto_network
        )
    }
}