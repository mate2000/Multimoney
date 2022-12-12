package com.multimoney.domain.model.credit

data class PromissoryNoteDetail(
    val totalRecords: Int?,
    val pageNumber: Int?,
    val pageSize: Int?,
    val movementsResultList: List<CreditMovementsResult>?
)
