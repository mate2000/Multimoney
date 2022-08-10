package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CompanyCanton
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryCompanyCantonUseCase {
    suspend operator fun invoke(pkUser: String, user: String, idBrand: Int):
            Flow<MultimoneyResult<List<CompanyCanton?>?>>
}