package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class MutationSendCreditContractEventUseCaseImpl(val creditRepository: CreditRepository) :
    MutationSendCreditContractEventUseCase {
    override suspend fun invoke(
        idImpresion: Long,
        idBrand: Int,
        link: String,
        active: Boolean,
        statusEvicertia: String,
        statusOnfido: String,
        currentStep: String
    ): Flow<MultimoneyResult<CreditContractEvent?>> = creditRepository.mutationSendCreditContractEvent(
        idImpresion,
        idBrand,
        link,
        active,
        statusEvicertia,
        statusOnfido,
        currentStep
    )
}
