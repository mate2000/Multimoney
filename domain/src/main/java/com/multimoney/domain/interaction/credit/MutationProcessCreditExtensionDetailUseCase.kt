package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.ProcessCreditExtensionDetail
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationProcessCreditExtensionDetailUseCase {
    suspend operator fun invoke(
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
    ): Flow<MultimoneyResult<ProcessCreditExtensionDetail?>>
}
