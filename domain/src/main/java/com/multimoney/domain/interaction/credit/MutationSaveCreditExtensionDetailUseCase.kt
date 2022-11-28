package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditExtensionDetail
import com.multimoney.domain.model.util.MultimoneyResult
import kotlinx.coroutines.flow.Flow

interface MutationSaveCreditExtensionDetailUseCase {
    suspend operator fun invoke(
        pkUser: Int,
        idBrand: Int,
        user: String,
        accountNumber: String,
        amount: Double,
        month: Int,
        pkPromotionMonth: Int,
        nextPaymentDate: String,
        quota: Double,
        quotaTotal: Double,
        comissionDisbursement: Double,
        rateInterestNormalLoan: Double,
        rateInterestNormalRegular: Double,
        cicle: Int,
        idProduct: Int,
        descriptionPromotionTerm: String,
        pkPromotion: Int
    ): Flow<MultimoneyResult<CreditExtensionDetail?>>
}
