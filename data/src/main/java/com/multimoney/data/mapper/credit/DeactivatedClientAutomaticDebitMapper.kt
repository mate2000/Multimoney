package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.DeactivatedClientAutomaticDebitMutation
import com.multimoney.domain.model.credit.AutomaticDebit
import com.multimoney.domain.model.util.error.MessageError

private fun DeactivatedClientAutomaticDebitMutation.DeactivatedClientAutomaticDebit.mapMessageToDomainModel() =
    MessageError(
        status = status,
        message = message,
        detail = detail
    )

private fun DeactivatedClientAutomaticDebitMutation.DeactivatedClientAutomaticDebit.mapToDomainModel() = AutomaticDebit(
    isUpdated = isUpdate,
    messageError = mapMessageToDomainModel()
)

fun DeactivatedClientAutomaticDebitMutation.Data.mapToDomainModel() = deactivatedClientAutomaticDebit.mapToDomainModel()
