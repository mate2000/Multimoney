package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.SaveClientBankAccountMutation
import com.multimoney.domain.model.credit.SaveClientBankAccount
import com.multimoney.domain.model.util.error.MessageError

private fun SaveClientBankAccountMutation.SaveClientBankAccount.mapMessageToDomainModel() = MessageError(
    status = status,
    message = message,
    detail = detail
)

private fun SaveClientBankAccountMutation.SaveClientBankAccount.mapToDomainModel() = SaveClientBankAccount(
    isUpdate = isUpdate,
    messageError = mapMessageToDomainModel()
)

fun SaveClientBankAccountMutation.Data.mapToDomainModel() = saveClientBankAccount.mapToDomainModel()
