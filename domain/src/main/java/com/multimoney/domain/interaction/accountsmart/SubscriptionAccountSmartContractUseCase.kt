package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.AccountSmartContractResult
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SubscriptionAccountSmartContractUseCase {
    suspend operator fun invoke(
        idRequestSys: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<AccountSmartContractResult?>>
}