package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.DestinyAccount
import com.multimoney.domain.model.credit.ProcessPaymentList
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow

class MutationProcessPaymentListUseCaseImpl(private val creditRepository: CreditRepository) :
    MutationProcessPaymentListUseCase {
    override suspend fun invoke(
        user: String,
        idBrand: Int,
        customerId: Int,
        identification: String,
        originAccountNumber: String,
        destinyAccountNumber: String,
        currencyId: String,
        customerName: String,
        description: String,
        destinyAccount: List<DestinyAccount>,
        amount: Any
    ): Flow<MultimoneyResult<ProcessPaymentList?>> = creditRepository.mutationProcessPaymentList(
        user = user,
        idBrand = idBrand,
        customerId = customerId,
        identification = identification,
        originAccountNumber = originAccountNumber,
        destinyAccountNumber = destinyAccountNumber,
        currencyId = currencyId,
        customerName = customerName,
        description = description,
        destinyAccount = destinyAccount,
        amount = amount
    )
}
