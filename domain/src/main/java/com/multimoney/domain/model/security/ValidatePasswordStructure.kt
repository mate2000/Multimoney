package com.multimoney.domain.model.security

data class ValidatePasswordStructure(
    val data: List<String>,
    val status: Int,
    val message: String,
    val detail: String,
)
