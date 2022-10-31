package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.GeneralEconomicActivityResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGeneralEconomicActivityUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
    ): Flow<MultimoneyResult<GeneralEconomicActivityResult?>>
}
