package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.balance.BalanceCryptoAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetBalanceCryptoAccountUseCaseImpl(
    private val cryptoRepository: CryptoRepository
) : GetBalanceCryptoAccountUseCase {

    override suspend fun invoke(
        user: String,
        idBrand: Int,
        identification: String,
    ): Flow<MultimoneyResult<BalanceCryptoAccount>> {
        return cryptoRepository.getBalanceCryptoAccount(user, idBrand, identification)
    }
}