package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.AutomaticCardDebit
import kotlinx.coroutines.flow.Flow

interface MutationActivatedCardAutomaticDebitUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int,
        idCard: Long,
        cardMasked: String
    ): Flow<MultimoneyResult<AutomaticCardDebit?>>
}
