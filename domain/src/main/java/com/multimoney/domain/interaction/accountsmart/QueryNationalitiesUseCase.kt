package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.Nationalities
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryNationalitiesUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<Nationalities?>>
}