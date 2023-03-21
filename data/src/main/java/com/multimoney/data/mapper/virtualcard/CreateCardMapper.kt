package com.multimoney.data.mapper.virtualcard

import com.multimoney.data.networking.graphql.apollomodel.CreateCardVDMutation
import com.multimoney.domain.model.virtualcard.CreateCard

private fun CreateCardVDMutation.CreateCardVD.mapToDomainModel() = CreateCard(
    idCard = idCard.toString().toInt(),
    creationDate = creationDate,
    verified = verified,
    detail = detail,
    cardMasked = cardMasked,
    verificationValue = verificationValue,
    expirationMonth = expirationMonth,
    expirationYear = expirationYear,
    cardTokenId = cardTokenId,
    currencyDescription = currencyDescription,
    country = country,
    default = default
)

fun CreateCardVDMutation.Data.mapToDomainModel() = createCardVD.mapToDomainModel()
