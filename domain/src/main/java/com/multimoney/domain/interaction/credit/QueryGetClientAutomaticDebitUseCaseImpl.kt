package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryGetClientAutomaticDebitUseCaseImpl(val creditRepository: CreditRepository) :
    QueryGetClientAutomaticDebitUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoanClient: Int
    ): Flow<MultimoneyResult<List<ClientBankAccount?>?>> =
        creditRepository.queryGetClientAutomaticDebit(user, idBrand, idClient, idLoanClient)
}
