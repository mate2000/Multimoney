package com.multimoney.data.mapper.virtualcard

data class AddCardResponse(
    val applicationName: String,
    val userName: String,
    val isApproved: Boolean,
    val cardTokenId: String,
    val maskedCard: String,
    val apiStatus: String,
    val isDefault: Boolean,
    val cardFastFunds: Boolean
)