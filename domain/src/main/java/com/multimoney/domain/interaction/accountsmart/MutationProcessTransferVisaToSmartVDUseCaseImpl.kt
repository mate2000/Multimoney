package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.VisaSmartPayment
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationProcessTransferVisaToSmartVDUseCaseImpl @Inject constructor(
    private val smartAccountRepository: SmartAccountRepository
) : MutationProcessTransferVisaToSmartVDUseCase {
    override suspend operator fun invoke(
        idCard: Long,
        tokenNumber: Long,
        identification: String,
        amount: String,
        currency: Int,
        description: String,
        cardMasked: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<VisaSmartPayment?>> =
        smartAccountRepository.mutationProcessTransferVisaToSmartVD(
            idCard,
            tokenNumber,
            identification,
            amount,
            currency,
            description,
            cardMasked,
            user,
            idBrand
        )
}
