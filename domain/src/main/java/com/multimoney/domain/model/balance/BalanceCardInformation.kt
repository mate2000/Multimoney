package com.multimoney.domain.model.balance

data class BalanceCardInformation(
    val cardInformation: CardInformation?,
    val floatingBalance: String?,
    val allowUnLock: Boolean?,
    val disbursementCommission: String?,
    val interestRate: String?,
    val term: String?,
    val fullName: String?,
    val remission: Boolean?
)
