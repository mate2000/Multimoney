package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class SubscriptionAccountSmartContractUseCaseImpl(val repository: SmartAccountRepository) :
    SubscriptionAccountSmartContractUseCase {
    override suspend fun invoke(
        idRequestSys: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<AccountSmartContractResult?>> =
        repository.subscriptionAccountContractEvent(idBrand, idRequestSys)
}