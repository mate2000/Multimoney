package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.LocalTransferResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow

class MutationProcessLocalTransferUseCaseImpl(val repository: SmartAccountRepository) :
    MutationProcessLocalTransferUseCase {
    override suspend operator fun invoke(
        pkUsuario: Int,
        user: String,
        idBrand: Int,
        originIdentification: String,
        idCurrencyOrigin: String,
        destinationIdentification: String,
        idCurrencyDestination: String,
        destinationAccountNumber: String,
        amount: Double,
        reason: String,
        accountToken: Long,
        exchangeRate: Double
    ): Flow<MultimoneyResult<LocalTransferResult?>> = repository.mutationProcessLocalTransfer(
        pkUsuario,
        user,
        idBrand,
        originIdentification,
        idCurrencyOrigin,
        destinationIdentification,
        idCurrencyDestination,
        destinationAccountNumber,
        amount,
        reason,
        accountToken,
        exchangeRate
    )
}
