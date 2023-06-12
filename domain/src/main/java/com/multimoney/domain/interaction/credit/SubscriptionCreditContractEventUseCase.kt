package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface SubscriptionCreditContractEventUseCase {
    suspend operator fun invoke(idPrint: Long, idBrand: Int): Flow<MultimoneyResult<CreditContractEvent?>>
}
