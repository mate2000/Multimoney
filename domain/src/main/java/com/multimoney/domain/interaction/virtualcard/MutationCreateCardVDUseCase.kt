package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CreateCard
import kotlinx.coroutines.flow.Flow

interface MutationCreateCardVDUseCase {
    suspend operator fun invoke(
        identification: String,
        cardTokenID: String,
        default: Boolean,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CreateCard?>>
}