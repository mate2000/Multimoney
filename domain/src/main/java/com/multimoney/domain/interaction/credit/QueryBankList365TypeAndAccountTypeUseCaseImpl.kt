package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.BankList365TypeAndAccountType
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class QueryBankList365TypeAndAccountTypeUseCaseImpl(val creditRepository: CreditRepository) :
    QueryBankList365TypeAndAccountTypeUseCase {
    override suspend fun invoke(idBrand: Int, user: String): Flow<MultimoneyResult<BankList365TypeAndAccountType?>> =
        creditRepository.queryBankList365TypeAndAccountType(idBrand, user)
}