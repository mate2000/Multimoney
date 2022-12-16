package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.PayCreditVisaDirect
import kotlinx.coroutines.flow.Flow

interface MutationPayCreditVDUseCase {
    suspend operator fun invoke(
        identification: String,
        currency: String,
        paymentAmount: Double,
        operationNumber: String,
        reference: String,
        comment: String,
        cardMasked: String,
        idCard: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<PayCreditVisaDirect?>>
}
