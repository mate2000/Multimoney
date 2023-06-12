package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.VisaSmartPayment
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationProcessTransferVisaToSmartVDUseCase {
    suspend operator fun invoke(
        idCard: Long,
        tokenNumber: Long,
        identification: String,
        amount: String,
        currency: Int,
        description: String,
        cardMasked: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<VisaSmartPayment?>>
}
