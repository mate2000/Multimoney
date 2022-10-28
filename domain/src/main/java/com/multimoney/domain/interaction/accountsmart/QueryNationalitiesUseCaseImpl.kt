package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.Nationalities
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryNationalitiesUseCaseImpl(val repository: SmartAccountRepository) :
    QueryNationalitiesUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<Nationalities?>> = repository.queryNationality(user, idBrand)
}