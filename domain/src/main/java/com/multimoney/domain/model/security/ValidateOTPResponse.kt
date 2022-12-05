package com.multimoney.domain.model.security

data class ValidateOTPResponse(
    val status : Int?,
    val message : String?,
    val detail : String?,
)
