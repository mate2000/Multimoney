package com.multimoney.domain.model.virtualcard

data class UpdateCard(
    val applicationName: String?,
    val userName: String?,
    val isApproved: Boolean?,
    val apiStatus: String?,
    val default: Boolean?,
)