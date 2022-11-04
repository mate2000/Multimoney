package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.AddressesLevel
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryAddressLevelOneUseCaseImpl(val repository: SmartAccountRepository) : QueryAddressLevelOneUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<AddressesLevel?>> =
        repository.queryAddressLevelOne(user, idBrand)
}
