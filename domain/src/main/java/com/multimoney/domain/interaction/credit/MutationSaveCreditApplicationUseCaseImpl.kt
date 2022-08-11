package com.multimoney.domain.interaction.credit

import com.multimoney.domain.repository.CreditRepository

class MutationSaveCreditApplicationUseCaseImpl(private val creditRepository: CreditRepository) :
    MutationSaveCreditApplicationUseCase {
    override suspend fun invoke(
        idUserRequest: Int,
        pkUser: Int,
        descPromotion: String,
        interestRate: String,
        symbolCurrency: String,
        descCurrency: String,
        idProduct: Int,
        idPromotion: Int,
        months: String,
        commissionPercentage: String,
        paymentDate: String,
        paymentAmount: String,
        user: String,
        idBrand: Int,
        selectedAmount: Double,
        minimumAmount: Double,
        creditLimit: Double,
        tractAmount: Double
    ) = creditRepository.mutationSaveCreditApplication(
        idUserRequest,
        pkUser,
        descPromotion,
        interestRate,
        symbolCurrency,
        descCurrency,
        idProduct,
        idPromotion,
        months,
        commissionPercentage,
        paymentDate,
        paymentAmount,
        user,
        idBrand,
        selectedAmount,
        minimumAmount,
        creditLimit,
        tractAmount
    )
}