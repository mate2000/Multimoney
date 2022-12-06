package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.SaveClientBankAccount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationSaveClientBankAccountUseCaseImpl @Inject constructor(val creditRepository: CreditRepository) :
    MutationSaveClientBankAccountUseCase {
    override suspend fun invoke(
        idClient: Long,
        idBank: Int,
        accountNumber: String,
        idCurrency: Int,
        idAccountType: Int?,
        idLoanClient: Long,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveClientBankAccount?>> =
        creditRepository.mutationSaveClientBankAccount(
            idClient = idClient,
            idBank = idBank,
            accountNumber = accountNumber,
            idCurrency = idCurrency,
            idAccountType = idAccountType,
            idLoanClient = idLoanClient,
            user = user,
            idBrand = idBrand
        )
}
