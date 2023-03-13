package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.BankList365TypeAndAccountType
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryBankList365TypeAndAccountTypeUseCase {
    suspend operator fun invoke(
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<BankList365TypeAndAccountType?>>
}