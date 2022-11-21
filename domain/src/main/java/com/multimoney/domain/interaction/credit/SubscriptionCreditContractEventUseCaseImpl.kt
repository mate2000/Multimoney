package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class SubscriptionCreditContractEventUseCaseImpl(val creditRepository: CreditRepository) :
    SubscriptionCreditContractEventUseCase {
    override suspend fun invoke(idPrint: Long, idBrand: Int): Flow<MultimoneyResult<CreditContractEvent?>> =
        creditRepository.subscriptionCreditContractEvent(idPrint, idBrand)
}
