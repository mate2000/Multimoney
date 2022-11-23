package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditExtensionMessage
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QueryCreditExtensionMessageUseCaseImpl @Inject constructor(val creditRepository: CreditRepository) :
    QueryCreditExtensionMessageUseCase {
    override suspend fun invoke(
        idClient: Long,
        currency: String,
        user: String,
        idBrand: Int,
        amountRequest: Double,
        idLoanClient: Long,
        quotaMax: Double,
        idProductBase: Int,
        cicle: Int
    ): Flow<MultimoneyResult<CreditExtensionMessage?>> =
        creditRepository.queryCreditExtensionMessage(
            idClient,
            currency,
            user,
            idBrand,
            amountRequest,
            idLoanClient,
            quotaMax,
            idProductBase,
            cicle
        )
}
