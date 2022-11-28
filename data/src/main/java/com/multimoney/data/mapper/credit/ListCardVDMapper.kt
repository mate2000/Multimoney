package com.multimoney.data.mapper.credit

import com.multimoney.data.networking.graphql.apollomodel.ListCardVDQuery
import com.multimoney.domain.model.credit.CardVisaDirect

private fun ListCardVDQuery.ListCardVD.mapToDomainModel() = CardVisaDirect(
    idCard = idCard.toString().toInt(),
    creationDate = creationDate.toString(),
    verified = verified,
    detail = detail,
    cardMaskedNumber = cardMasked,
    expirationMonth = expirationMonth,
    expirationYear = expirationYear,
    cardTokenId = cardTokenId,
    currencyDescription = currencyDescription,
    country = country
)

fun ListCardVDQuery.Data.mapToDomainModel() = listCardVD.map { it.mapToDomainModel() }
