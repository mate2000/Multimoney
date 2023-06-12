package com.multimoney.domain.model.credit

data class CreditExtensionProduct(
    val pkPromotion: Int?,
    val datePayActuality: String?,
    val strDatePayActuality: String?,
    val rateInterestNormal: String?,
    val strRateInterestNormal: String?,
    val comissionDisbursement: String?,
    val strComissionDisbursement: String?,
    val month: String?,
    val quota: Double?,
    val quotaTotal: Double?,
    val strQuota: String?,
    val strQuotaTotal: String?,
    val cicle: Int?,
    val descriptionPromotionTerm: String?
)
