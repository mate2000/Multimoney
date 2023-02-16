package com.multimoney.data.mapper.crypto

import com.multimoney.data.networking.graphql.apollomodel.GetCryptoMovementsQuery
import com.multimoney.domain.model.crypto.CryptoCurrencyMovement
import com.multimoney.domain.model.crypto.CryptoCurrencyMovements
import com.multimoney.domain.model.crypto.GetCryptoCurrencyMovements

fun GetCryptoMovementsQuery.Data.mapToDomainModel() = GetCryptoCurrencyMovements(
    cryptoCurrencyMovements = cryptoCurrencyMovement.mapToDomainModel()
)

private fun GetCryptoMovementsQuery.CryptoCurrencyMovement.mapToDomainModel() = CryptoCurrencyMovements(
    totalCount = total_count ?: 0,
    items = items?.map { it?.mapToDomainModel() ?: CryptoCurrencyMovement() } ?: listOf()
)

private fun GetCryptoMovementsQuery.Item.mapToDomainModel() = CryptoCurrencyMovement(
    abbreviationCurrency = abbreviationCurrency.orEmpty(),
    amountFilled = amount_filled.orEmpty(),
    baseAmount = base_amount.orEmpty(),
    dateCreated = dateCreated.orEmpty(),
    createdAt = created_at.toString(),
    held = true ?: false,
    market = market.orEmpty(),
    modifiedAt = modified_at.toString(),
    id = id.orEmpty(),
    descriptionMovement = decriptionMovement.orEmpty(),
    price = price.orEmpty(),
    profileId = profile_id.orEmpty(),
    quoteAmount = quote_amount.orEmpty(),
    side = side.orEmpty(),
    type = type.orEmpty(),
    monthLimitExceeded = month_limit_exceeded ?: false,
    volumeWeightedAveragePrice = volume_weighted_average_price.orEmpty()
)
