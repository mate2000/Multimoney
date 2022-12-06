package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditApplication
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSaveCreditApplicationUseCase {
    suspend operator fun invoke(
        idUserRequest: Int,
        pkUser: Int,
        descPromotion: String,
        interestRate: String?,
        symbolCurrency: String,
        descCurrency: String,
        idProduct: Int,
        idPromotion: Int,
        months: String?,
        commissionPercentage: String?,
        paymentDate: String,
        paymentAmount: String,
        user: String,
        idBrand: Int,
        selectedAmount: Double,
        minimumAmount: Double?,
        creditLimit: Double?,
        tractAmount: Double?,
        currentStep: String
    ): Flow<MultimoneyResult<CreditApplication?>>
}
