package com.multimoney.domain.model.accountsmart

data class SmartMovement(
    val idTransaction: Int,
    val creationDate: String,
    val transactionCatalogueDescription: String,
    val amount: Double,
    val currencyDescription: String,
    val bankAuthorization: String,
    val idSubTransaction: Int,
    val sign: String
)
