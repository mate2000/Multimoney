package com.multimoney.domain.model.credit

import com.multimoney.domain.model.util.error.MessageError

data class SaveClientBankAccount(
    val isUpdate: Boolean?,
    val messageError: MessageError
)