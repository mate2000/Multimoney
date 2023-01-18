package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.UpdateCard
import kotlinx.coroutines.flow.Flow

interface MutationUpdateCardVDUseCase {
    suspend operator fun invoke(
        idCard: Long,
        identification: String,
        cardDescription: String,
        cardMasked: String,
        expirationMonth: String,
        expirationYear: String,
        verificationValue: String,
        default: Boolean,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<UpdateCard?>>
}