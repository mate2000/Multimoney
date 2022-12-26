package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.GetCardAutomaticDebitQuery
import com.multimoney.domain.model.virtualcard.CardVisaDirect

private fun GetCardAutomaticDebitQuery.GetCardAutomaticDebit.mapToDomainModel() = CardVisaDirect(
    idCard = idCard?.toString()?.toInt() ?: 0,
    cardMaskedNumber = cardMasked,
    debitDate = fechaDebito
)

fun GetCardAutomaticDebitQuery.Data.mapToDomainModel() = getCardAutomaticDebit?.map { it?.mapToDomainModel() }
