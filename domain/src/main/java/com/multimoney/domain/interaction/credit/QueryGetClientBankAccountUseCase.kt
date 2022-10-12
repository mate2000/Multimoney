package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.ClientBankAccount
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetClientBankAccountUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        idClient: Int,
        idLoan: Int
    ): Flow<MultimoneyResult<List<ClientBankAccount?>?>>
}
