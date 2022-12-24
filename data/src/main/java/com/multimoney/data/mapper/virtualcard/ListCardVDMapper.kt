package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.ListCardVDQuery
import com.multimoney.domain.model.virtualcard.CardVisaDirect

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
