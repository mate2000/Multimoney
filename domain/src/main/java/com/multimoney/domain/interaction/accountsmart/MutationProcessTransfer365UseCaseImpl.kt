package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.Transfer365Result
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationProcessTransfer365UseCaseImpl(
    val repository: SmartAccountRepository
) : MutationProcessTransfer365UseCase {
    override suspend fun invoke(
        identification: String,
        destinationAccount: String,
        destinationBankId: String,
        destinationType: String,
        typeAccountId: String,
        destinationName: String,
        destinationLastName: String,
        amount: Double,
        motive: String,
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<Transfer365Result?>> {
        return repository.mutationProcessTransfer365(
            idBrand = idBrand,
            user = user,
            identification = identification,
            destinationAccount = destinationAccount,
            destinationBankId = destinationBankId,
            destinationName = destinationName,
            destinationLastName = destinationLastName,
            destinationType = destinationType,
            typeAccountId = typeAccountId,
            amount = amount,
            motive = motive
        )
    }
}