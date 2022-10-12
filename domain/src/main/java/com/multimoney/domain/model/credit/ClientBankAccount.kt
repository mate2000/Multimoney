package com.multimoney.domain.model.credit

data class ClientBankAccount(
    val id: Int?,
    val idBank: Int?,
    val bankDescription: String?,
    val accountNumber: String?,
    val idCurrency: Int?,
    val idCurrencyDestination: Int?
)
