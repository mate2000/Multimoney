package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.BankListTransfer365
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryBankListTransfer365UseCase {
    suspend operator fun invoke(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<BankListTransfer365?>>
}