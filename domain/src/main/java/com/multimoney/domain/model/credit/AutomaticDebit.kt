package com.multimoney.domain.model.credit

import com.multimoney.domain.model.util.error.MessageError

data class AutomaticDebit(
    val isUpdated: Boolean?,
    val messageError: MessageError?
)
