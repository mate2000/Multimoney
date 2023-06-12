package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.AutomaticDebit
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationDeactivateCardAutomaticDebitUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        idCard: Long
    ): Flow<MultimoneyResult<AutomaticDebit?>>
}
