package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.ActivatedClientAutomaticDebitMutation
import com.multimoney.domain.model.credit.AutomaticDebit
import com.multimoney.domain.model.util.error.MessageError

private fun ActivatedClientAutomaticDebitMutation.ActivatedClientAutomaticDebit.mapMessageToDomainModel() =
    MessageError(
        status = status,
        message = message,
        detail = detail
    )

private fun ActivatedClientAutomaticDebitMutation.ActivatedClientAutomaticDebit.mapToDomainModel() = AutomaticDebit(
    isUpdated = isUpdate,
    messageError = mapMessageToDomainModel()
)

fun ActivatedClientAutomaticDebitMutation.Data.mapToDomainModel() = activatedClientAutomaticDebit.mapToDomainModel()
