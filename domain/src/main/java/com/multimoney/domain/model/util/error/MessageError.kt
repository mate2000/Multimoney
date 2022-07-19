package com.multimoney.domain.model.util.error

data class MessageError(
    val status: Int?,
    val message: String?,
    val detail: String?
)