package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.CivilStatusResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryCivilStatusUseCaseImpl(private val smartAccountRepository: SmartAccountRepository) :
    QueryCivilStatusUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<CivilStatusResult?>> = smartAccountRepository.queryCivilStatus(user, idBrand)
}