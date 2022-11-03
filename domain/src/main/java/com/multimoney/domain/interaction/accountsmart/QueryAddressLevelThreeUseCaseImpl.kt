package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.AddressesLevel
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryAddressLevelThreeUseCaseImpl(val repository: SmartAccountRepository) : QueryAddressLevelThreeUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idAddressLevelOne: String,
        idAddressLevelTwo: String,
    ): Flow<MultimoneyResult<AddressesLevel?>> =
        repository.queryAddressLevelThree(user, idBrand, idAddressLevelOne, idAddressLevelTwo)
}