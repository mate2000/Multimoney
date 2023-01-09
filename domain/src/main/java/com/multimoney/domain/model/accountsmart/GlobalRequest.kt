package com.multimoney.domain.model.accountsmart

data class GlobalRequest(
    val idGlobalRequest: Long,
    val accountExists: Boolean,
    val idSysRequest: Int
)
