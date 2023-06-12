package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.ProcessCreditExtensionDetail
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationProcessCreditExtensionDetailUseCaseImpl @Inject constructor(val creditRepository: CreditRepository) :
    MutationProcessCreditExtensionDetailUseCase {
    override suspend fun invoke(
        user: String,
        pkUser: Int,
        idBrand: Int,
        idFlowControl: Any,
        currency: String,
        accountNumber: String,
        bankAccount: String,
        idBankAccount: Any,
        idLoanForm: Any,
        loanForm: String,
        idLoanClient: Int,
        phoneNumber: String,
        userEmail: String
    ): Flow<MultimoneyResult<ProcessCreditExtensionDetail?>> =
        creditRepository.mutationProcessCreditExtensionDetail(
            user,
            pkUser,
            idBrand,
            idFlowControl,
            currency,
            accountNumber,
            bankAccount,
            idBankAccount,
            idLoanForm,
            loanForm,
            idLoanClient,
            phoneNumber,
            userEmail
        )
}
