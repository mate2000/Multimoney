package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.DestinyAccount
import com.multimoney.domain.model.credit.ProcessPaymentList
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationProcessPaymentListUseCase {
    suspend operator fun invoke(
        user: String,
        idBrand: Int,
        customerId: Int,
        identification: String,
        originAccountNumber: String,
        destinyAccountNumber: String,
        currencyId: String,
        customerName: String,
        description: String,
        destinyAccount: List<DestinyAccount>,
        amount: Any
    ): Flow<MultimoneyResult<ProcessPaymentList?>>
}
