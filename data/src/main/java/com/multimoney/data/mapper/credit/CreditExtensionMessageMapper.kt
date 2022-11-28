package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.CreditExtensionMessageQuery
import com.multimoney.domain.model.credit.CreditExtensionMessage
import com.multimoney.domain.model.credit.CreditExtensionProduct

private fun CreditExtensionMessageQuery.Product.mapToDomainModel() = CreditExtensionProduct(
    pkPromotion = pkPromotion.toString().toInt(),
    datePayActuality = datePayActuality.toString(),
    strDatePayActuality = strDatePayActuality,
    rateInterestNormal = rateInterestNormal,
    strRateInterestNormal = strRateInterestNormal,
    comissionDisbursement = comissionDisbursement,
    strComissionDisbursement = strComissionDisbursement,
    month = month,
    quota = quota.toString().toDouble(),
    quotaTotal = quotaTotal.toString().toDouble(),
    strQuota = strQuota,
    strQuotaTotal = strQuotaTotal,
    cicle = cicle,
    descriptionPromotionTerm = descriptionPromotionTerm
)

private fun CreditExtensionMessageQuery.CreditExtensionMessage.mapToDomainModel() = CreditExtensionMessage(
    pkPromotionMonth = pkPromotionMonth,
    product = product?.map { it.mapToDomainModel() }
)

fun CreditExtensionMessageQuery.Data.mapToDomainModel() = creditExtensionMessage.mapToDomainModel()
