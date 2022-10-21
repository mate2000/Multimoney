package com.multimoney.domain.repository

import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SmartAccountRepository {
    suspend fun queryCivilStatus(
        pkUser: Int,
        idBrand: Int
    ): Flow<MultimoneyResult<CreditOffer?>>
}