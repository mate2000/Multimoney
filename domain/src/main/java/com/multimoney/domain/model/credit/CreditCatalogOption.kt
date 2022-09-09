package com.multimoney.domain.model.credit

data class CreditCatalogOption(
    val id: Int,
    val description: String,
    val pkCatalog: String?,
    val fkCatalog: Int
)