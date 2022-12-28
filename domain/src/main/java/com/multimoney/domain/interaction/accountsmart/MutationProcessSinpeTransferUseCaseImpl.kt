package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SinpeTransferResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
import com.multimoney.domain.repository.SmartAccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationProcessSinpeTransferUseCaseImpl @Inject constructor(
    private val smartAccountRepository: SmartAccountRepository
) : MutationProcessSinpeTransferUseCase {
    override suspend fun invoke(
        pkUser: Int,
        identification: String,
        originCustomerIdentification: String,
        ibanAccountOrigin: String,
        originCustomerName: String,
        idCurrencyOrigin: String,
        ibanAccountDestination: String,
        destinationCustomerIdentification: String,
        destinationCustomerName: String,
        idCurrencyDestination: String,
        reasonOfTransfer: String,
        transferType: SmartSinpeTransferType,
        amountToTransfer: Double,
        exchangeRate: Double,
        idBrand: Int,
        user: String
    ): Flow<MultimoneyResult<SinpeTransferResult?>> {
        return smartAccountRepository.mutationProcessSinpeTransfer(
            pkUser = pkUser,
            identification = identification,
            originCustomerIdentification = originCustomerIdentification,
            ibanAccountOrigin = ibanAccountOrigin,
            originCustomerName = originCustomerName,
            idCurrencyOrigin = idCurrencyOrigin,
            ibanAccountDestination = ibanAccountDestination,
            destinationCustomerIdentification = destinationCustomerIdentification,
            destinationCustomerName = destinationCustomerName,
            idCurrencyDestination = idCurrencyDestination,
            reasonOfTransfer = reasonOfTransfer,
            transferType = transferType,
            amountToTransfer = amountToTransfer,
            exchangeRate = exchangeRate,
            idBrand = idBrand,
            user = user
        )
    }
}