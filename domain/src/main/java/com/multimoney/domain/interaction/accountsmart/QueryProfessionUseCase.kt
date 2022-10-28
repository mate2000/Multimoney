package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.Professions
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryProfessionUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<Professions?>>
}