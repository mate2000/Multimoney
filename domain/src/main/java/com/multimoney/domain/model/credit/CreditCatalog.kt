package com.multimoney.domain.model.credit

data class CreditCatalog(
    val pkQuestionOption: Int?,
    val fkQuestion: Int?,
    val controlType: String?,
    val description: String?,
    val pkCatalog: String?,
    val isCatalogBrandOffice: Boolean?,
    val useValue: Boolean?,
    val isCoreCatalog: Boolean?,
    val pkForm: String?,
    val valueCatalog: String?,
    val maximumAmount: String?,
    var value: String?,
    val subOptions: List<CreditCatalogOption?>?
)