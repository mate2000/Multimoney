package com.multimoney.domain.model.virtualcard

import com.multimoney.domain.model.util.error.MessageError

data class DeleteCard(
    val isApproved: String?,
    val apiStatus: String?,
    val messageError: MessageError?
)