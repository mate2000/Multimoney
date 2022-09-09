package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditInfoQuestion
import com.multimoney.domain.model.credit.SaveCreditFlowStep
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class MutationSaveCreditFlowStepUseCaseImpl(val creditRepository: CreditRepository) : MutationSaveCreditFlowStepUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        infoQuestion: List<CreditInfoQuestion?>,
        idLogUserRequest: Int,
        idUser: Int,
        currentStep: String
    ): Flow<MultimoneyResult<SaveCreditFlowStep?>?> =
        creditRepository.mutationSaveCreditFlowStep(user, idBrand, infoQuestion, idLogUserRequest, idUser, currentStep)
}