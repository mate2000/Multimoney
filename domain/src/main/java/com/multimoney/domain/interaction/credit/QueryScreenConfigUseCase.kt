package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryScreenConfigUseCase {
    suspend operator fun invoke(
        pkUser: String,
        user: String,
        idBrand: Int,
        idUserRequest: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>
}