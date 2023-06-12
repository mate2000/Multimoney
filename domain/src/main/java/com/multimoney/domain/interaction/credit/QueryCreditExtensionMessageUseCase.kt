package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditExtensionMessage
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryCreditExtensionMessageUseCase {
    suspend operator fun invoke(
        idClient: Long,
        currency: String,
        user: String,
        idBrand: Int,
        amountRequest: Double,
        idLoanClient: Long,
        quotaMax: Double,
        idProductBase: Int,
        cicle: Int
    ): Flow<MultimoneyResult<CreditExtensionMessage?>>
}
