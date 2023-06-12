package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.AutomaticDebit
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class MutationActivateClientAutomaticDebitUseCaseImpl(private val creditRepository: CreditRepository) :
    MutationActivateClientAutomaticDebitUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idClient: Long,
        idLoanClient: Long,
        origin: String,
        idAccount: Long,
        idCurrency: Int
    ): Flow<MultimoneyResult<AutomaticDebit?>> = creditRepository.mutationActivateClientAutomaticDebit(
        user = user,
        idBrand = idBrand,
        idClient = idClient,
        idLoanClient = idLoanClient,
        origin = origin,
        idAccount = idAccount,
        idCurrency = idCurrency

    )
}
