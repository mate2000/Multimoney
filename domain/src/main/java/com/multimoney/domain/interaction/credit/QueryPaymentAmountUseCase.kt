package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.PaymentAmount
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryPaymentAmountUseCase {
    suspend operator fun invoke(
        amount: Int,
        months: String?,
        idProduct: String,
        currencySymbol: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<PaymentAmount?>>
}