package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.GeneralEconomicActivityResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryGeneralEconomicActivityUseCaseImpl(
    private val smartAccountRepository: SmartAccountRepository
) : QueryGeneralEconomicActivityUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<GeneralEconomicActivityResult?>> =
        smartAccountRepository.queryGeneralEconomicActivity(user, idBrand)
}
