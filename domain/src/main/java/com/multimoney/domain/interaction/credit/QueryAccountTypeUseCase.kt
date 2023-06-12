package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.RegularExpressionList
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryAccountTypeUseCase {
    suspend fun invoke(user: String, idBrand: Int): Flow<MultimoneyResult<RegularExpressionList?>>
}