package com.multimoney.domain.interaction.mmvisa

import com.multimoney.domain.model.mmvisa.CardIssuanceNV
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryCardIssuanceNVUseCase {
    suspend operator fun invoke(
        idClient: Long,
        requestType: String,
        identification: String,
        idLoanClient: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardIssuanceNV?>>
}
