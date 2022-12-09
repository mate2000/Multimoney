package com.multimoney.domain.interaction.mmvisa

import com.multimoney.domain.model.mmvisa.CardIssuanceNV
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.MultimoneyVisaRepository
import kotlinx.coroutines.flow.Flow

class QueryCardIssuanceNVUseCaseImpl(val repository: MultimoneyVisaRepository) :
    QueryCardIssuanceNVUseCase {
    override suspend fun invoke(
        idClient: Long,
        requestType: String,
        identification: String,
        idLoanClient: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardIssuanceNV?>> = repository.queryCardIssuanceNV(
        idClient,
        requestType,
        identification,
        idLoanClient,
        user,
        idBrand
    )
}
