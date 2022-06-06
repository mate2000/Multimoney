package com.multimoney.domain.repository

import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface BalanceRepository {
    suspend fun queryBalance(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: String,
        idLoanClient: Int
    ): Flow<MultimoneyResult<Balance?>>
}