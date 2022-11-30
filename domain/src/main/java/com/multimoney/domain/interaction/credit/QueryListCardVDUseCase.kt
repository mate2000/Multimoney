package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CardVisaDirect
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryListCardVDUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        identification: String
    ): Flow<MultimoneyResult<List<CardVisaDirect?>?>>
}
