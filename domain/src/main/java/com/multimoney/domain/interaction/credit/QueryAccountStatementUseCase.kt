package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.AccountStatement
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryAccountStatementUseCase {
    suspend operator fun invoke(
        creditNumber: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<AccountStatement?>>
}
