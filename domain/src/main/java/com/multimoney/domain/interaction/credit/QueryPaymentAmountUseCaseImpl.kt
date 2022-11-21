package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class QueryPaymentAmountUseCaseImpl(private val creditRepository: CreditRepository) : QueryPaymentAmountUseCase {
    override suspend fun invoke(
        amount: Int,
        months: String?,
        idProduct: String,
        currencySymbol: String,
        user: String,
        idBrand: Int
    ) = creditRepository.queryPaymentAmount(amount, months, idProduct, currencySymbol, user, idBrand)
}