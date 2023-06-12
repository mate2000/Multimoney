package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CardBlocking
import kotlinx.coroutines.flow.Flow

interface MutationCardBlockingUseCase {
    suspend operator fun invoke(
        blockType: String,
        observations: String,
        idClient: Int,
        userApp: String,
        cardToken: String,
        source: String,
        idLoan: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardBlocking?>>
}
