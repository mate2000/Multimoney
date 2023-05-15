package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.BanksAmpliationList
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryBanksAmpliationUseCaseImpl(val repository: CreditRepository) :
    QueryBanksAmpliationUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<BanksAmpliationList?>> = repository.queryBanksAmpliation(user, idBrand)
}