package com.multimoney.domain.model.metrics

data class BaseEventDataDto(
    val user: String? = null,
    val idBrand: Int? = null,
    val idClient: Int? = null,
    val idLoanClient: Int? = null,
    val identification: String? = null
)
