package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.Transfer365Result
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationProcessTransfer365MobileUseCaseImpl(
    val repository: SmartAccountRepository
) : MutationProcessTransfer365MobileUseCase {
    override suspend fun invoke(
        identification: String,
        phoneNumber: String,
        destinationBankId: String,
        typeAccountId: String,
        destinationName: String,
        destinationLastName: String,
        amount: Double,
        motive: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<Transfer365Result?>> {
        return repository.mutationProcessTransfer365Mobile(
            idBrand = idBrand,
            user = user,
            identification = identification,
            phoneNumber = phoneNumber,
            destinationBankId = destinationBankId,
            destinationName = destinationName,
            destinationLastName = destinationLastName,
            typeAccountId = typeAccountId,
            amount = amount,
            motive = motive
        )
    }
}