package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.AutomaticDebit
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class MutationDeactivateClientAutomaticDebitUseCaseImpl(private val creditRepository: CreditRepository) :
    MutationDeactivateClientAutomaticDebitUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        origin: String,
        idAccount: Long
    ): Flow<MultimoneyResult<AutomaticDebit?>> = creditRepository.mutationDeactivateClientAutomaticDebit(
        user = user,
        idBrand = idBrand,
        idClient = idClient,
        idLoanClient = idLoanClient,
        origin = origin,
        idAccount = idAccount
    )
}
