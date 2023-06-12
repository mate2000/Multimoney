package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.AutomaticDebit
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationActivateClientAutomaticDebitUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        origin: String,
        idAccount: Long,
        idCurrency: Int
    ): Flow<MultimoneyResult<AutomaticDebit?>>
}
