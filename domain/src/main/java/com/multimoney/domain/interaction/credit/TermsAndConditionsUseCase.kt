package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditCatalog
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface TermsAndConditionsUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        systemInDarkTheme: Boolean
    ): Flow<MultimoneyResult<String>>
}