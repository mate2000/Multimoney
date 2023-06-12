package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SmartAccountTypeResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QuerySmartAccountTypeUseCaseImpl(
    val repository: SmartAccountRepository
) : QuerySmartAccountTypeUseCase {
    override suspend fun invoke(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SmartAccountTypeResult?>> {
        return repository.querySmartAccountType(
            idBrand = idBrand,
            user = user
        )
    }
}