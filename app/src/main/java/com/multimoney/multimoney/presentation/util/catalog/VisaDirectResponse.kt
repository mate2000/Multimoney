package com.multimoney.multimoney.presentation.util.catalog

data class VisaDirectResponse(
    val applicationName: String?,
    val userName: String?,
    val isApproved: Boolean?,
    val cardTokenId: String?,
    val maskedCard: String?,
    val apiStatus: String?,
    val isDefault: Boolean?,
    val cardFastFunds: String?
)
