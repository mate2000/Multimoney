package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.ValidateDepositAddressResponse
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class ValidateDepositAddressUseCaseImpl(
    private val cryptoRepository: CryptoRepository
): ValidateDepositAddressUseCase {

    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
        market: String,
        address: String
    ): Flow<MultimoneyResult<ValidateDepositAddressResponse>> {
        return cryptoRepository.validateDepositAddress(user, idBrand, identification, market, address)
    }
}
