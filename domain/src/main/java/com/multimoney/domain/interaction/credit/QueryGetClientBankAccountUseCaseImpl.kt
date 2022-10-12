package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryGetClientBankAccountUseCaseImpl(val creditRepository: CreditRepository) : QueryGetClientBankAccountUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoan: Int
    ): Flow<MultimoneyResult<List<ClientBankAccount?>?>> =
        creditRepository.queryGetClientBankAccount(user, idBrand, idClient, idLoan)
}
