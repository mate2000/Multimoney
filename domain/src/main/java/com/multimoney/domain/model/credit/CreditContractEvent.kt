package com.multimoney.domain.model.credit

data class CreditContractEvent(
    val idPrint: Long,
    val idBrand: Int?,
    val link: String?,
    val statusEvicertia: String?,
    val statusOnfido: String?,
    val active: Boolean?,
    val currentStep: String?
)
