package com.multimoney.domain.interaction.accountsmart

import com.multimoney.domain.model.accountsmart.SinpeTransferResult
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.model.util.catalog.SmartSinpeTransferType
import kotlinx.coroutines.flow.Flow

interface MutationProcessSinpeTransferUseCase {
    suspend operator fun invoke(
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
    ): Flow<MultimoneyResult<SinpeTransferResult?>>
}