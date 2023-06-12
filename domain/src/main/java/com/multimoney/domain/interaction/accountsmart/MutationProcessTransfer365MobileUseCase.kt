package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.Transfer365Result
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationProcessTransfer365MobileUseCase {
    suspend operator fun invoke(
        identification: String,
        phoneNumber: String,
        destinationBankId: String,
        typeAccountId: String,
        destinationName: String,
        destinationLastName: String,
        amount: Double,
        motive: String,
        user: String,
        idBrand: Int,
        destinationType: String
    ): Flow<MultimoneyResult<Transfer365Result?>>
}