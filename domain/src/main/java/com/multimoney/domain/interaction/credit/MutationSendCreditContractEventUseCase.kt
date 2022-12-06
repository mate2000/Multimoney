package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditContractEvent
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSendCreditContractEventUseCase {
    suspend operator fun invoke(
        idImpresion: Long,
        idBrand: Int,
        link: String,
        active: Boolean,
        statusEvicertia: String,
        statusOnfido: String,
        currentStep: String
    ): Flow<MultimoneyResult<CreditContractEvent?>>
}
