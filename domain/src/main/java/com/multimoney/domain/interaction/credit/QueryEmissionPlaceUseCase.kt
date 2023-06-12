package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryEmissionPlaceUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        idUserRequest: Int,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>
}