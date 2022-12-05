package com.multimoney.domain.repository

import com.multimoney.domain.model.mmvisa.CardIssuanceNV
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MultimoneyVisaRepository {

    suspend fun queryCardIssuanceNV(
        idClient: Long,
        requestType: String,
        identification: String,
        idLoanClient: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardIssuanceNV?>>
}
