package com.multimoney.domain.model.credit

data class CreditCatalogOption(
    val description: String?,
    val pkCatalog: String?,
    val fkCatalog: Int?,
    val valueCatalog: String? = null
)
