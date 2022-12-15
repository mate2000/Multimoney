package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import kotlinx.coroutines.flow.Flow

interface QueryListCardVDUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identification: String
    ): Flow<MultimoneyResult<List<CardVisaDirect?>?>>
}
