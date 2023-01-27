package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.BankListTransfer365
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QueryBankListTransfer365UseCaseImpl(
    val repository: SmartAccountRepository
) : QueryBankListTransfer365UseCase {
    override suspend fun invoke(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<BankListTransfer365?>> {
        return repository.queryBankListTransfer365(
            idBrand = idBrand,
            user = user
        )
    }
}