package com.multimoney.domain.model.credit

data class SaveCreditOperation(
    val idPrint: Long,
    val showFinalScreen: Boolean,
    val showOutSchedule: Boolean
)
