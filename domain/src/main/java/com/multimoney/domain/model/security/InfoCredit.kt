package com.multimoney.domain.model.security

data class InfoCredit(
    val idClient: Int?,
    val idLoanClient: Int?,
    val status: Int?,
    val amountAvailable: Double
)