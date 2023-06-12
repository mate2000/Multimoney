package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CardUnblocking
import kotlinx.coroutines.flow.Flow

interface MutationCardUnblockingUseCase {
    suspend operator fun invoke(
        observations: String,
        idClient: Int,
        userApp: String,
        cardToken: String,
        source: String,
        idLoan: Int,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CardUnblocking?>>
}
