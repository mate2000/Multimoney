package com.multimoney.domain.interaction.credit

import com.multimoney.domain.model.credit.CreditExtensionDetail
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MutationSaveCreditExtensionDetailUseCaseImpl @Inject constructor(val creditRepository: CreditRepository) :
    MutationSaveCreditExtensionDetailUseCase {
    override suspend fun invoke(
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
    ): Flow<MultimoneyResult<CreditExtensionDetail?>> =
        creditRepository.mutationSaveCreditExtensionDetail(
            pkUser,
            idBrand,
            user,
            accountNumber,
            amount,
            month,
            pkPromotionMonth,
            nextPaymentDate,
            quota,
            quotaTotal,
            comissionDisbursement,
            rateInterestNormalLoan,
            rateInterestNormalRegular,
            cicle,
            idProduct,
            descriptionPromotionTerm,
            pkPromotion
        )
}
