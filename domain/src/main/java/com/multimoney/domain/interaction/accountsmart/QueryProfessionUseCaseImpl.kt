package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.Professions
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryProfessionUseCaseImpl(private val smartAccountRepository: SmartAccountRepository) :
    QueryProfessionUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<Professions?>> =
        smartAccountRepository.queryProfessions(user, idBrand)
}