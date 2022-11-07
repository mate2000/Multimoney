package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryCompanyProvinceUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        idUserRequest: Int
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>
}
