package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.SaveTermsAndConditionsCredit
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationSaveTermsAndConditionsCreditUseCaseImpl @Inject constructor(val creditRepository: CreditRepository) :
    MutationSaveTermsAndConditionsCreditUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        pkUser: Long,
        currentFlow: String,
        identification: String
    ): Flow<MultimoneyResult<SaveTermsAndConditionsCredit?>> =
        creditRepository.mutationSaveTermsAndConditionsCredit(
            user = user,
            idBrand = idBrand,
            pkUser = pkUser,
            currentFlow = currentFlow,
            identification = identification
        )
}