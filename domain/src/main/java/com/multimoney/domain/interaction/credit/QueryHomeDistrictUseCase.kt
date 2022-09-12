package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryHomeDistrictUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        user: String,
        idBrand: Int,
        fkCatalogIdentifier: String,
        idUserRequest:String
    ): Flow<MultimoneyResult<List<CreditCatalog?>?>>
}