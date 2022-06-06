package com.multimoney.domain.interaction.balance

import com.multimoney.domain.model.balance.Balance
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryBalanceUseCase {
    suspend operator fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: String,
        idLoanClient: Int
    ): Flow<MultimoneyResult<Balance?>>
}