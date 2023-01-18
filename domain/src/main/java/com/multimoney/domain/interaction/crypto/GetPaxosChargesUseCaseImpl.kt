package com.multimoney.domain.interaction.crypto

import com.multimoney.domain.model.crypto.GetPaxosChargeData
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class GetPaxosChargesUseCaseImpl(val repository: CryptoRepository) : GetPaxosChargesUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        key: String,
        transaction: String
    ): Flow<MultimoneyResult<GetPaxosChargeData>> {
        return repository.queryGetPaxosCharges(user, idBrand, key, transaction)
    }
}