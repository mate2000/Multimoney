package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.AccountSmartForBuyCrypto
import com.multimoney.domain.model.accountsmart.Beneficiary
import com.multimoney.domain.model.accountsmart.GlobalRequest
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QuerySmartAccountsUseCase {
    suspend operator fun invoke(
        user: String,
        identification: String,
        idBrand: Int,
        accountStatus: Int
    ): Flow<MultimoneyResult<List<AccountSmartForBuyCrypto>?>>
}
