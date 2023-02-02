package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.AccountSmartForBuyCrypto
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class QuerySmartAccountsUseCaseImpl(val repository: SmartAccountRepository) :
    QuerySmartAccountsUseCase {
    override suspend fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        accountStatus: Int
    ): Flow<MultimoneyResult<List<AccountSmartForBuyCrypto>?>> = repository.querySmartAccounts(
        user,
        identification,
        idBrand,
        accountStatus
    )
}
