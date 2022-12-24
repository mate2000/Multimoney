package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.SaveTermsAndConditionsCredit
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSaveTermsAndConditionsCreditUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        pkUser: Long,
        currentFlow: String,
        identification: String
    ): Flow<MultimoneyResult<SaveTermsAndConditionsCredit?>>
}