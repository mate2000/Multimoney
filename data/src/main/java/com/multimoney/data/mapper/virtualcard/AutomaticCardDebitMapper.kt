package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.ActivatedCardAutomaticDebitMutation
import com.multimoney.domain.model.virtualcard.AutomaticCardDebit

private fun ActivatedCardAutomaticDebitMutation.ActivatedCardAutomaticDebit.mapToDomainModel() =
    AutomaticCardDebit(isUpdated = isUpdate)

fun ActivatedCardAutomaticDebitMutation.Data.mapToDomainModel() = activatedCardAutomaticDebit.mapToDomainModel()
