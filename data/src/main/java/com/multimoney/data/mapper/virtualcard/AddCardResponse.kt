package com.multimoney.data.mapper.virtualcard

data class AddCardResponse(
    val isApproved: Boolean,
    val cardTokenId: String,
    val apiStatus: String
)