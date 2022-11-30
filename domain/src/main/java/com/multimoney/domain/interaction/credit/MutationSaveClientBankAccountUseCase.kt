package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.SaveClientBankAccount
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSaveClientBankAccountUseCase {
    suspend operator fun invoke(
        idClient: Long,
        idBank: Int,
        accountNumber: String,
        idCurrency: Int,
        idAccountType: Int,
        idLoanClient: Long,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<SaveClientBankAccount?>>
}