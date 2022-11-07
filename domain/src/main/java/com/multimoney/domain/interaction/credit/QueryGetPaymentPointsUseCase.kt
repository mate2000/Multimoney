package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.PaymentPoint
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface QueryGetPaymentPointsUseCase {
    suspend operator fun invoke(
        idBrand: Int
    ): Flow<MultimoneyResult<List<PaymentPoint?>?>>
}
