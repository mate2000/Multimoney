package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.SaveCreditOffer
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSaveCreditOfferUseCase {
    suspend operator fun invoke(
        pkUser: Long,
        idUserRequest: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveCreditOffer?>>
}