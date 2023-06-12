package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.CivilStatusResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryCivilStatusUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<CivilStatusResult?>>
}