package com.multimoney.domain.model.credit

data class CatalogSubOptions(
    val id: Int,
    val controlType: String,
    val description: String,
    val pkCatalog: String?,
    val fkCatalog: Int,
    val intern: String?
)