package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.AddressesLevel
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryAddressLevelTwoUseCaseImpl(val repository: SmartAccountRepository) : QueryAddressLevelTwoUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idAddressLevelOne: String
    ): Flow<MultimoneyResult<AddressesLevel?>> =
        repository.queryAddressLevelTwo(user, idBrand, idAddressLevelOne)
}
