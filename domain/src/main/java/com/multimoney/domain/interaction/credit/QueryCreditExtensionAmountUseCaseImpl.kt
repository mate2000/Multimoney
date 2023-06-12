package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditExtensionAmount
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QueryCreditExtensionAmountUseCaseImpl @Inject constructor(val creditRepository: CreditRepository) :
    QueryCreditExtensionAmountUseCase {
    override suspend fun invoke(
        idClient: Long,
        currency: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CreditExtensionAmount?>> =
        creditRepository.queryCreditExtensionAmount(idClient, currency, user, idBrand)
}
