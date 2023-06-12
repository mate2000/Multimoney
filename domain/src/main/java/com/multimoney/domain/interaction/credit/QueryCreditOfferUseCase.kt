package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditOffer
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryCreditOfferUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        idBrand: Int
    ): Flow<MultimoneyResult<CreditOffer?>>
}