package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.GetParametersMobileByCategory
import kotlinx.coroutines.flow.Flow

interface QueryGetParametersMobileByCategoryUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        category: String
    ): Flow<MultimoneyResult<List<GetParametersMobileByCategory?>?>>
}