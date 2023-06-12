package com.multimoney.domain.interaction.virtualcard

import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.virtualcard.PayCreditVisaDirect
import com.multimoney.domain.repository.VirtualCardRepository
import kotlinx.coroutines.flow.Flow

class MutationPayCreditVDUseCaseImpl(private val virtualCardRepository: VirtualCardRepository) :
    MutationPayCreditVDUseCase {

    override suspend fun invoke(
        identification: String,
        currency: String,
        paymentAmount: Double,
        operationNumber: String,
        reference: String,
        comment: String,
        cardMasked: String,
        idCard: Long,
        idBrand: Int
    ): Flow<MultimoneyResult<PayCreditVisaDirect?>> =
        virtualCardRepository.mutationPayCreditVD(
            identification,
            currency,
            paymentAmount,
            operationNumber,
            reference,
            comment,
            cardMasked,
            idCard,
            idBrand
        )
}
