package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.DeactivatedCardAutomaticDebitMutation
import com.multimoney.domain.model.credit.AutomaticDebit

private fun DeactivatedCardAutomaticDebitMutation.DeactivatedCardAutomaticDebit.mapToDomainModel() = AutomaticDebit(
    isUpdated = isUpdate,
    messageError = null
)

fun DeactivatedCardAutomaticDebitMutation.Data.mapToDomainModel() = deactivatedCardAutomaticDebit.mapToDomainModel()
