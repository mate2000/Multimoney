package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.CardVisaDirect
import kotlinx.coroutines.flow.Flow

interface QueryGetCardAutomaticDebitUseCase {
    suspend operator fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long
    ): Flow<MultimoneyResult<List<CardVisaDirect?>?>>
}
