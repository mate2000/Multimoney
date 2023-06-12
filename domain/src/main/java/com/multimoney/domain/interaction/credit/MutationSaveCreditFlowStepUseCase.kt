package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditInfoQuestion
import com.multimoney.domain.model.credit.SaveCreditFlowStep
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSaveCreditFlowStepUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        infoQuestion: List<CreditInfoQuestion>,
        idLogUserRequest: Int,
        idUser: Int,
        currentStep: String
    ): Flow<MultimoneyResult<SaveCreditFlowStep?>>
}
