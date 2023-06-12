package com.multimoney.domain.model.credit

data class CreditExtensionAmount(
    val amountMax: Double?,
    val amountMin: Double?,
    val labelAmountMaxAvailable: String?,
    val labelAmountMinAvailable: String?,
    val symbol: String?,
    val amountTract: Int?,
    val idLoanClient: Long?,
    val idProductBase: Int?,
    val cicle: Int?,
    val quotaMax: Double?,
    val promissoryNote: String?,
    val amountMinDisbursementCK: Double?,
    val paramMore: Boolean?
)
