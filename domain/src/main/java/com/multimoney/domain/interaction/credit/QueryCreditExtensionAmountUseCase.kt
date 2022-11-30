package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditExtensionAmount
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryCreditExtensionAmountUseCase {
    suspend operator fun invoke(
        idClient: Long,
        currency: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CreditExtensionAmount?>>
}
