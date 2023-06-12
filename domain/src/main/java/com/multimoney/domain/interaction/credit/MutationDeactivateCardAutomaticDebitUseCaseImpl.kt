package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.AutomaticDebit
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class MutationDeactivateCardAutomaticDebitUseCaseImpl(private val creditRepository: CreditRepository) :
    MutationDeactivateCardAutomaticDebitUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        idCard: Long
    ): Flow<MultimoneyResult<AutomaticDebit?>> = creditRepository.mutationDeactivateCardAutomaticDebit(
        user = user,
        idBrand = idBrand,
        idClient = idClient,
        idLoanClient = idLoanClient,
        idCard = idCard
    )
}
